package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import cz.zcu.kiv.crce.classmodel.processor.tools.MapTools;

public class Endpoint {


    private Set<EndpointType> types = new HashSet<>();
    private Map<String, Object> expectedResponse;
    private String response = "None";
    private String uri;
    private String baseUrl = "";

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
        POST, GET, PUT, PATCH, DELETE, HEAD, OPTIONS;
    }

    public Set<EndpointType> getTypes() {
        return this.types;
    }

    public Endpoint addType(EndpointType type) {
        this.types.add(type);
        return this;
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
     * 
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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

    private String responseToString() {

        final String RESPDELIMETER = ", ";

        if (expectedResponse == null) {
            return response;
        }
        response = "";
        for (Map.Entry<String, Object> item : MapTools.mapToList(this.expectedResponse)) {
            response += item.getKey() + "=" + item.getValue() + RESPDELIMETER;
        }
        return response.substring(0, response.length() - RESPDELIMETER.length());
    }

    @Override
    public String toString() {
        return "Endpoint{" + "baseUrl=" + baseUrl + ", uri='" + uri + '\'' + ", type='"
                + this.types.toString() + '\'' + ", expectedResponse='" + responseToString()
                + "' }'";
    }

    /**
     * @return the expectedResponse
     */
    public Map<String, Object> getExpectedResponse() {
        return expectedResponse;
    }

    /**
     * @param expectedResponse the expectedResponse to set
     */
    public void setExpectedResponse(Map<String, Object> expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public void setExpectedResponse(String response) {
        this.response = response;
    }
}
