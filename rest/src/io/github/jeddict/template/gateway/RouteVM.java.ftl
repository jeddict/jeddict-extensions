package ${package};

import java.util.List;

/**
 * View Model that stores a route managed by the Gateway.
 */
public class RouteVM {

    private String path;

    private String serviceId;

    private List<ServiceInstanceVM> serviceInstances;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<ServiceInstanceVM> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstanceVM> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }
}
