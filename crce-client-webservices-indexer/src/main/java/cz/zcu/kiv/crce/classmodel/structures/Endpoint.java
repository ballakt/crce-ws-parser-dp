package cz.zcu.kiv.crce.classmodel.structures;

import java.util.HashSet;
import java.util.Set;

public class Endpoint {


    private Set<EndpointType> types = new HashSet<>();
    private String uri;

    public Endpoint(String uri, Set<EndpointType> types) {
        this.uri = uri;
        this.types = types;
    }

    public Endpoint(String uri, EndpointType type) {
        this.uri = uri;
        this.types.add(type);
    }

    public Endpoint() {
    }

    public enum EndpointType {
        POST, GET, PUT, PATCH, DELETE;
    }

    public Set<EndpointType> getTypes() {
        return this.types;
    }

    public void addType(EndpointType type) {
        this.types.add(type);
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }


    public boolean equals(Endpoint endpoint) {
        if (!uri.equals(endpoint.getUri())) {
            return false;
        }
        if (this.types.equals(endpoint.getTypes())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Endpoint{" + "uri='" + uri + '\'' + ", type='" + this.types.toString() + '\'' + '}';
    }
}
