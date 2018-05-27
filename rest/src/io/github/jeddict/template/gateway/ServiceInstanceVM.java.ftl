package ${package};

import java.net.URI;
import java.util.List;

public class ServiceInstanceVM {

    /**
     * service uri address
     */
    private URI uri;

    /**
     * status of the registered ServiceInstanceVM
     */
    private String status;

    /**
     * tags associated with the service instance
     */
    private List<String> tags;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
