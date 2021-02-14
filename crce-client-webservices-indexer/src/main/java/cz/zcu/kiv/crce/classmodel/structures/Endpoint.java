package cz.zcu.kiv.crce.classmodel.structures;

public class Endpoint {


    private EndpointType type;
    private String uri;

    public Endpoint(String uri, EndpointType type) {
        this.uri = uri;
        this.type = type;
    }

    public Endpoint() {
        this.uri = uri;
        this.type = type;
    }

    public enum EndpointType {
        PUT, GET, DELETE, POST;
    }

    /**
     * @return the type
     */
    public EndpointType getType() {
        return type;
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

    /**
     * @param type the type to set
     */
    public void setType(EndpointType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Endpoint{" + "uri='" + uri + '\'' + ", type='" + this.type + '\'' + '}';
    }
}
