package ${package};

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.constants.ZuulHeaders;
import com.netflix.zuul.context.RequestContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import jakarta.inject.Inject;

@ApplicationScoped
public class SendResponseFilter extends ZuulFilter {

    @Inject
    private Logger log;

    private static final int INITIAL_STREAM_BUFFER_SIZE = 1024;

    private static final boolean SET_CONTENT_LENGTH = false;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1000;
    }

    @Override
    public boolean shouldFilter() {
        return "/ms".equals(RequestContext.getCurrentContext().getRequest().getServletPath());<#-- 
        return !RequestContext.getCurrentContext().getZuulResponseHeaders().isEmpty()
                || RequestContext.getCurrentContext().getResponseDataStream() != null
                || RequestContext.getCurrentContext().getResponseBody() != null;-->
    }

    @Override
    public Object run() {
        try {
            addResponseHeaders();
            writeResponse();
        } catch (IOException ex) {
            log.error(null, ex);
        }
        return null;
    }

    private void writeResponse() throws IOException {
        RequestContext context = RequestContext.getCurrentContext();
        if (context.getResponseBody() == null && context.getResponseDataStream() == null) {
            return;
        }

        HttpServletResponse servletResponse = context.getResponse();
        servletResponse.setCharacterEncoding("UTF-8");

        OutputStream outStream = servletResponse.getOutputStream();
        InputStream is = null;
        try {
            if (context.getResponseBody() != null) {
                String body = context.getResponseBody();
                writeResponse(new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8"))), outStream);
                return;
            }

            boolean isGzipRequested = false;
            final String requestEncoding = context.getRequest().getHeader(ZuulHeaders.ACCEPT_ENCODING);
            if (requestEncoding != null && requestEncoding.equals("gzip")) {
                isGzipRequested = true;
            }

            is = context.getResponseDataStream();
            InputStream inputStream = is;
            if (is != null) {
                if (context.sendZuulResponse()) {
                    if (context.getResponseGZipped() && !isGzipRequested) {
                        try {
                            inputStream = new GZIPInputStream(is);
                        } catch (ZipException e) {
                            log.error("gzip expected but not received assuming unencoded response" + context.getRequest().getRequestURL().toString());
                            inputStream = is;
                        }
                    } else if (context.getResponseGZipped() && isGzipRequested) {
                        servletResponse.setHeader(ZuulHeaders.CONTENT_ENCODING, "gzip");
                    }
                    writeResponse(inputStream, outStream);
                }
            }

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
            }
        }
    }

    private void writeResponse(InputStream zin, OutputStream out) throws IOException {
        byte[] bytes = new byte[INITIAL_STREAM_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = zin.read(bytes)) != -1) {

            try {
                out.write(bytes, 0, bytesRead);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // doubles buffer size if previous read filled it
            if (bytesRead == bytes.length) {
                bytes = new byte[bytes.length * 2];
            }
        }
    }

    private void addResponseHeaders() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse servletResponse = context.getResponse();
        List<Pair<String, String>> zuulResponseHeaders = context.getZuulResponseHeaders();
        zuulResponseHeaders.forEach(pair -> {
            servletResponse.addHeader(pair.first(), pair.second());
        });

        Long contentLength = context.getOriginContentLength();

        // only inserts Content-Length if origin provides it and origin response is not gzipped
        if (SET_CONTENT_LENGTH) {
            if (contentLength != null && !context.getResponseGZipped()) {
                servletResponse.setContentLength(contentLength.intValue());
            }
        }
    }

}
