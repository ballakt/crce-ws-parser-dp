package cz.zcu.kiv.crce.rest;

import java.io.Serializable;
import java.util.Set;

class Endpoint implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = -9157814198634818855L;

    private String path;
    private Set<String> httpMethods;
    private Set<String> consumes;
    private Set<String> produces;

    /* ------------------------------------- */
    private EndpointRequestBody body;
    private Set<EndpointResponse> responses; // expected response
    private Set<RequestParameter> parameters;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(Set<String> httpMethods) {
        this.httpMethods = httpMethods;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethods.add(httpMethod);
    }

    public Set<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<String> consumes) {
        this.consumes = consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes.add(consumes);
    }

    public Set<String> getProduces() {
        return produces;
    }

    public void setProduces(Set<String> produces) {
        this.produces = produces;
    }

    public void setProduces(String produces) {
        this.produces.add(produces);
    }

    public Set<EndpointResponse> getResponses() {
        return responses;
    }

    public void setResponses(Set<EndpointResponse> responses) {
        this.responses = responses;
    }

    public void setResponse(EndpointResponse response) {
        this.responses.add(response);
    }

    public Set<RequestParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<RequestParameter> parameters) {
        this.parameters = parameters;
    }

    public EndpointRequestBody getBody() {
        return body;
    }

    public void setBody(EndpointRequestBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Endpoint{path=" + path + ", httpMethods='" + httpMethods + '\'' + ", consumes="
                + consumes + ", produces=" + produces + ", responses=" + responses + ", parameters="
                + parameters + '}';
    }
}
