package ${package};

import java.net.URISyntaxException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.UriBuilder;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the
 * <a href="https://developer.github.com/v3/#pagination">Github API</a>, and
 * follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link
 * header)</a>.
 */
public class PaginationUtil {

    public static ResponseBuilder generatePaginationHttpHeaders(ResponseBuilder builder, Page page, String baseUrl)
            throws URISyntaxException {

        builder.header("X-Total-Count", Integer.toString(page.getTotalElements()));
        String link = "";
        if ((page.getNumber() + 1) < page.getTotalPages()) {
            link = "<" + generateUri(baseUrl, page.getNumber() + 1, page.getSize()) + ">; rel=\"next\",";
        }
        // prev link
        if ((page.getNumber()) > 0) {
            link += "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
        }
        // last and first link
        int lastPage = 0;
        if (page.getTotalPages() > 0) {
            lastPage = page.getTotalPages() - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
        builder.header(HttpHeaders.LINK, link);
        return builder;
    }

    private static String generateUri(String baseUrl, int page, int size) throws URISyntaxException {
        UriBuilder builder = UriBuilder.fromPath(baseUrl);
        builder.queryParam("page", page);
        builder.queryParam("size", size);
        return builder.build().toString();
    }

}
