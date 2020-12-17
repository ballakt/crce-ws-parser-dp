package cz.zcu.kiv.crce.rest;

public class MockedExpectedValuesSpring {

    private final String path = "spring-rest/foos";
    private final String httpMethod = "GET";
    private final String consumesType = "applications/json";
    private final String producesType = "applications/json";
    private final String structure = String.class.toString();
    private Endpoint mockEndpoint;

    public void MockedExpectedValues() {
        this.mockEndpoint = new Endpoint();
        this.mockEndpoint.setPath(path);
        this.mockEndpoint.setConsumes(consumesType);
        this.mockEndpoint.setHttpMethod(httpMethod);
        this.mockEndpoint.setProduces(producesType);
        EndpointResponse response = new EndpointResponse();
        response.setStatus(0);
        response.setArray(false);
        response.setStructure(structure);
        this.mockEndpoint.setResponse(response);
    }

    public Endpoint getMockEndpoint() {
        return this.mockEndpoint;
    }

}
