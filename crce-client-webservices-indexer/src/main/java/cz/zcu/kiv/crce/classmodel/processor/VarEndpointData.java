package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Set;
import cz.zcu.kiv.crce.classmodel.definition.Header;
import cz.zcu.kiv.crce.rest.EndpointParameter;

public class VarEndpointData extends Endpoint {

    /**
     *
     */
    private static final long serialVersionUID = 7312540085684152917L;

    public VarEndpointData(String baseUrl, String path, HttpMethod httpMethod,
            Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
            Set<EndpointParameter> parameters, Header produces, Header consumes) {
        this.baseUrl = baseUrl;
        this.path = path;
        this.httpMethods.add(httpMethod);
        this.requestBodies = requestBodies;
        this.expectedResponses = expectedResponses;
        this.parameters = parameters;
        this.produces.add(produces);
        this.consumes.add(consumes);
    }

    public VarEndpointData() {
    };
}
