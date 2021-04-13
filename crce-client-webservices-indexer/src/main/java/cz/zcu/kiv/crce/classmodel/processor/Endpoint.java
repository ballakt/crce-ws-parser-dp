package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.EndpointParameter;

public class Endpoint {

    protected String path;
    protected String baseUrl;
    protected Set<HttpMethod> httpMethods;
    protected Set<EndpointParameter> parameters;
    protected Set<EndpointRequestBody> requestBodies;
    protected Set<EndpointRequestBody> expectedResponses;
    protected Set<String> produces;


    public Endpoint(String path, Set<HttpMethod> httpMethods) {
        this.path = path;
        this.httpMethods = httpMethods;
    }

    public Endpoint(String baseUrl, String path, Set<HttpMethod> httpMethods,
            Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
            Set<EndpointParameter> parameters, Set<String> produces) {
        this(path, httpMethods, requestBodies, expectedResponses, parameters, produces);
        this.baseUrl = baseUrl;
    }

    public Endpoint(String path, Set<HttpMethod> httpMethods,
            Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
            Set<EndpointParameter> parameters, Set<String> produces) {
        this.setPath(path);
        this.httpMethods = httpMethods;
        this.requestBodies = requestBodies;
        this.expectedResponses = expectedResponses;
        this.parameters = parameters;
        this.produces = produces;
    }

    public Endpoint(String path, HttpMethod type) {
        this();
        this.setPath(path);
        httpMethods.add(type);
    }

    public Endpoint() {
        this.httpMethods = new HashSet<>();
        this.requestBodies = new HashSet<>();
        this.expectedResponses = new HashSet<>();
        this.parameters = new HashSet<>();
        this.produces = new HashSet<>();
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

    public Endpoint addParameter(EndpointParameter param) {
        parameters.add(param);
        return this;
    }

    public Set<EndpointParameter> getParameters() {
        return this.parameters;
    }

    public Endpoint addRequestBody(EndpointRequestBody body) {
        this.requestBodies.add(body);
        return this;
    }

    public Endpoint addExpectedResponse(EndpointRequestBody response) {
        this.expectedResponses.add(response);
        return this;
    }

    public Set<EndpointRequestBody> getExpectedResponses() {
        return this.expectedResponses;
    }

    /**
     * 
     * @param baseUrl
     */
    public Endpoint setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public void merge(Endpoint endpoint) {
        String newPath = endpoint.getPath() != null ? endpoint.getPath() : this.path;
        String newBaseUrl = endpoint.getBaseUrl() != null ? endpoint.getBaseUrl() : this.baseUrl;
        this.setPath(newPath);
        this.setBaseUrl(newBaseUrl);
        this.httpMethods.addAll(endpoint.getHttpMethods());
        this.requestBodies.addAll(endpoint.getRequestBodies());
        this.expectedResponses.addAll(endpoint.getExpectedResponses());
        this.parameters.addAll(endpoint.getParameters());
    }

    public boolean sEquals(Endpoint endpoint) {
        return super.equals(endpoint);
    }


    public boolean equals(Endpoint endpoint) {
        final boolean httpMethodEq = httpMethods.equals(endpoint.getHttpMethods());
        final boolean reqBodiesEq = requestBodies.containsAll(endpoint.getRequestBodies());
        final boolean expectedResponsesEq =
                expectedResponses.containsAll(endpoint.getExpectedResponses());
        final boolean parametersEq = parameters.containsAll(endpoint.getParameters());
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrl() {
        if (path == null && baseUrl == null) {
            return null;
        }

        if (path == null) {
            return baseUrl;
        }

        if (baseUrl == null) {
            return path;
        }

        return baseUrl + path;
    }

    /**
     * @param path the path to set
     */
    public Endpoint setPath(String path) {

        if (path != null) {
            this.path = path.replace("//", "");
            if (this.path.endsWith("/")) {
                this.path = this.path.substring(0, this.path.length() - 1);
            }
        }

        return this;
    }

    /**
     * @return the produces
     */
    public Set<String> getProduces() {
        return produces;
    }

    /**
     * @param produces the produces to set
     */
    public Endpoint addProduces(String produces) {
        this.produces.add(produces);
        return this;
    }

}
