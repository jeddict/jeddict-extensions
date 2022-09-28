package ${package};

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.net.ssl.SSLContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.ClientBuilder;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;

@ApplicationScoped
public class RoutingFilter extends ZuulFilter {

    private static final String CONTENT_ENCODING = "Content-Encoding";
    
    @Inject
    private Logger log;

    private final AtomicReference<CloseableHttpClient> CLIENT = new AtomicReference<>(newClient());

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public boolean shouldFilter() {
        return "/ms".equals(RequestContext.getCurrentContext().getRequest().getServletPath());
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        InputStream requestEntity = null;
        try {
            HttpServletRequest request = context.getRequest();
            requestEntity = request.getInputStream();
            String verb = request.getMethod().toUpperCase();
            Header[] headers = buildZuulRequestHeaders(request);
            String uri = request.getRequestURI();
            if (context.getRequest().getPathInfo() != null) {
                uri = context.getRequest().getPathInfo();
            }
            System.out.println("URI " + uri);
            CloseableHttpClient httpclient = CLIENT.get();
            HttpResponse response = forward(httpclient, verb, uri, request, headers, requestEntity);
            setResponse(response);

        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            try {
                requestEntity.close();
            } catch (IOException ex) {
            }
        }
        return null;
    }

    private HttpHost getHttpHost() {
        URL host = RequestContext.getCurrentContext().getRouteHost();
        return new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
    }

    private String getQueryString() {
        String encoding = "UTF-8";
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String currentQueryString = request.getQueryString();
        if (currentQueryString == null || currentQueryString.equals("")) {
            return "";
        }

        String rebuiltQueryString = "";
        for (String keyPair : currentQueryString.split("&")) {
            if (rebuiltQueryString.length() > 0) {
                rebuiltQueryString = rebuiltQueryString + "&";
            }

            if (keyPair.contains("=")) {
                String pair[] = keyPair.split("=", 2);
                String name = pair[0];
                String value = pair[1];
                try {
                    value = URLDecoder.decode(value, encoding);
                } catch (UnsupportedEncodingException ex) {
                    log.error(null, ex);
                }
                try {
                    value = new URI(null, null, null, value, null).toString().substring(1);
                } catch (URISyntaxException ex) {
                    log.error(null, ex);
                }
                value = value.replaceAll("&", "%26");
                rebuiltQueryString = rebuiltQueryString + name + "=" + value;
            } else {
                String value = null;
                try {
                    value = URLDecoder.decode(keyPair, encoding);
                } catch (UnsupportedEncodingException ex) {
                    log.error(null, ex);
                }
                try {
                    value = new URI(null, null, null, value, null).toString().substring(1);
                } catch (URISyntaxException ex) {
                    log.error(null, ex);
                }
                rebuiltQueryString = rebuiltQueryString + value;
            }
        }
        return "?" + rebuiltQueryString;
    }

    private HttpResponse forward(CloseableHttpClient httpclient, String verb, String uri, HttpServletRequest request, Header[] headers, InputStream requestEntity) throws IOException {
        HttpHost httpHost = getHttpHost();
        HttpRequest httpRequest;
        InputStreamEntity entity;
        switch (verb) {
            case "POST":
                entity = new InputStreamEntity(requestEntity, request.getContentLength());
                HttpPost httpPost = new HttpPost(uri + getQueryString());
                httpPost.setEntity(entity);
                httpRequest = httpPost;
                break;
            case "PUT":
                entity = new InputStreamEntity(requestEntity, request.getContentLength());
                HttpPut httpPut = new HttpPut(uri + getQueryString());
                httpPut.setEntity(entity);
                httpRequest = httpPut;
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uri + getQueryString());
        }

        httpRequest.setHeaders(headers);
        return httpclient.execute(httpHost, httpRequest);
    }

    private void setResponse(HttpResponse response) throws IOException {
        RequestContext context = RequestContext.getCurrentContext();

        context.set("hostZuulResponse", response);
        context.setResponseStatusCode(response.getStatusLine().getStatusCode());
        context.setResponseDataStream(
                response.getEntity() != null ? response.getEntity().getContent() : null
        );

        boolean isOriginResponseGzipped = false;

        for (Header h : response.getHeaders(CONTENT_ENCODING)) {
            if (HTTPRequestUtils.getInstance().isGzipped(h.getValue())) {
                isOriginResponseGzipped = true;
                break;
            }
        }
        context.setResponseGZipped(isOriginResponseGzipped);

        for (Header header : response.getAllHeaders()) {
            context.addOriginResponseHeader(header.getName(), header.getValue());

            if (header.getName().equalsIgnoreCase("content-length")) {
                context.setOriginContentLength(header.getValue());
            }

            if (isValidHeader(header.getName())) {
                context.addZuulResponseHeader(header.getName(), header.getValue());
            }
        }

    }

    private Header[] buildZuulRequestHeaders(HttpServletRequest request) {

        List<BasicHeader> headers = new ArrayList();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = ((String) headerNames.nextElement()).toLowerCase();
            String value = request.getHeader(name);
            if (isValidHeader(name)) {
                headers.add(new BasicHeader(name, value));
            }
        }

        Map<String, String> zuulRequestHeaders = RequestContext.getCurrentContext().getZuulRequestHeaders();

        zuulRequestHeaders.forEach((k, v) -> {
            String name = k.toLowerCase();
            headers.stream()
                    .filter(he -> he.getName().equals(name))
                    .findAny()
                    .ifPresent(h -> headers.remove(h));

            headers.add(new BasicHeader(k, v));
        });

        if (RequestContext.getCurrentContext().getResponseGZipped()) {
            headers.add(new BasicHeader("accept-encoding", "deflate, gzip"));
        }
        return headers.toArray(new Header[headers.size()]);
    }

    private boolean isValidHeader(String name) {
        if (name.toLowerCase().contains("content-length")) {
            return false;
        }

        if (name.toLowerCase().equals("host")) {
            return false;
        }

        if (!RequestContext.getCurrentContext().getResponseGZipped()) {
            if (name.toLowerCase().contains("accept-encoding")) {
                return false;
            }
        }
        return true;
    }

    private CloseableHttpClient newClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(newConnectionManager());
        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        builder.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                return false;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                return null;
            }
        });

        return builder.build();
    }

    private HttpClientConnectionManager newConnectionManager() {
        SSLContext sslContext = SSLContexts.createSystemDefault();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(Integer.parseInt(System.getProperty("zuul.max.host.connections", "200")));
        cm.setDefaultMaxPerRoute(Integer.parseInt(System.getProperty("zuul.max.host.connections", "20")));
        return cm;
    }
}
