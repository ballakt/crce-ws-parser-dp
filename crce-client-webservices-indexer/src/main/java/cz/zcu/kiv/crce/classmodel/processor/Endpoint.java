package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.EndpointParameter;

public class Endpoint {

    private String path;
    private Set<HttpMethod> httpMethods;
    private Set<EndpointRequestBody> requestBodies;
    private Set<EndpointRequestBody> expectedResponses;
    private Set<EndpointParameter> parameters;


    /*
     * private Map<String, Object> responseObject; private Object[] responseArray; private String
     * responseSimple = "None";
     * 
     * private Map<String, Object> paramObject; private Object[] paramArray; private String
     * paramSimple = "None";
     */

    private String baseUrl = "";

    public Endpoint(String path, Set<HttpMethod> httpMethods) {
        this.path = path;
        this.httpMethods = httpMethods;
    }

    public Endpoint(String path, Set<HttpMethod> httpMethods,
            Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
            Set<EndpointParameter> parameters) {
        this.path = path;
        this.httpMethods = httpMethods;
        this.requestBodies = requestBodies;
        this.expectedResponses = expectedResponses;
        this.parameters = parameters;
    }

    public Endpoint(String path, HttpMethod type) {
        this.httpMethods = new HashSet<>();
        this.requestBodies = new HashSet<>();
        this.expectedResponses = new HashSet<>();
        this.parameters = new HashSet<>();
        this.path = path;
        httpMethods.add(type);
    }

    public Endpoint() {
        this.httpMethods = new HashSet<>();
        this.requestBodies = new HashSet<>();
        this.expectedResponses = new HashSet<>();
        this.parameters = new HashSet<>();
    }

    public enum HttpMethod {
        POST, GET, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE;
    }

    public Set<HttpMethod> getHttpMethods() {
        return this.httpMethods;
    }

    public Set<EndpointRequestBody> getRequestBodies() {
        return this.requestBodies;
    }

    public Endpoint addHttpMethod(HttpMethod type) {
        this.httpMethods.add(type);
        return this;
    }

    public void addParameter(EndpointParameter param) {
        parameters.add(param);
    }

    public Set<EndpointParameter> getParameters() {
        return this.parameters;
    }

    public void addRequestBody(EndpointRequestBody body) {
        this.requestBodies.add(body);
    }

    public void addExpectedResponse(EndpointRequestBody response) {
        this.expectedResponses.add(response);
    }

    public Set<EndpointRequestBody> getExpectedResponses() {
        return this.requestBodies;
    }
    /*
     * public void addBody(EndpointRequestBody body) { this.bodies.add(body); }
     */

    /**
     * 
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void merge(Endpoint endpoint) {
        this.httpMethods.addAll(endpoint.getHttpMethods());
        this.requestBodies.addAll(endpoint.getRequestBodies());
        this.expectedResponses.addAll(endpoint.getExpectedResponses());
        this.parameters.addAll(endpoint.getParameters());
    }

    public boolean equals(Endpoint obj) {
        final boolean httpMethodEq = httpMethods.equals(obj.getHttpMethods());
        final boolean reqBodiesEq = requestBodies.containsAll(obj.getRequestBodies());
        final boolean expectedResponsesEq =
                expectedResponses.containsAll(obj.getExpectedResponses());
        final boolean parametersEq = parameters.containsAll(obj.getParameters());
        return httpMethodEq && reqBodiesEq && expectedResponsesEq && parametersEq;
    }

    @Override
    public String toString() {
        return "Endpoint{baseUrl=" + baseUrl + ", path=" + path + ", httpMethods='" + httpMethods
                + '\'' + ", requestBodies=" + requestBodies + ", expectedResponses="
                + expectedResponses + ", parameters=" + parameters + "'}'";
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
