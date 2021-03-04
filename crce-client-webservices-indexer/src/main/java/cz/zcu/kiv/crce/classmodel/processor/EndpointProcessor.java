package cz.zcu.kiv.crce.classmodel.processor;

import java.util.LinkedList;
import java.util.List;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint.EndpointType;

class EndpointHandler extends NewMethodProcessor {

    private List<Endpoint> endpoints = new LinkedList<>();
    private Endpoint endpoint = null;
    private final String clienClassName =
            "org/springframework/web/reactive/function/client/WebClient";

    private final String invocationFunction = "retrieve";
    private final String objectRecieveType = "bodyToMono";

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    @Override
    protected void processCALL(Operation operation, StringBuilder value,
            StringBuilder aggregationValue) {


        if (operation.getDescription().startsWith(this.clienClassName)) {
            // client request handler

            final String funcName = operation.getFuncName();

            if (endpoint == null) {
                endpoint = new Endpoint();
            }

            if (funcName.equals("get")) {
                endpoint.setType(EndpointType.GET);
            } else if (funcName.equals("put")) {
                endpoint.setType(EndpointType.PUT);
            } else if (funcName.equals("post")) {
                endpoint.setType(EndpointType.POST);
            } else if (funcName.equals("delete")) {
                endpoint.setType(EndpointType.DELETE);
            } else if (funcName.equals("uri")) {
                if (aggregationValue.length() > 0) {
                    System.out.println("SET_URI=" + aggregationValue.toString());
                    endpoint.setUri(aggregationValue.toString());
                } else {
                    System.out.println("VALUE=" + value.toString());
                    endpoint.setUri(value.toString());
                }
            } else if (funcName.equals(invocationFunction)) {
                if (endpoint != null && endpoint.getUri() != null) {
                    this.endpoints.add(endpoint);
                }
                endpoint = null;
            } else if (funcName.equals(objectRecieveType)) {
                // TODO: ziskat data z tridy ktera se vklada do bodyToMono
            }

            // value.setLength(0);

            return;
        }
        super.processCALL(operation, value, aggregationValue);
    }

    @Override
    public void process(MethodWrapper method) {
        this.endpoint = null;
        super.process(method);
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }
}


public class EndpointProcessor extends ClassProcessor {
    private EndpointHandler endpointHandler;
    private List<Endpoint> endpoints = null;

    public EndpointProcessor(ClassMap classes) {
        super(classes);
        this.endpointHandler = new EndpointHandler(classes);
    }

    @Override
    public void process(ClassWrapper class_) {
        super.process(class_);
        for (MethodWrapper method : class_.getMethods()) {
            endpointHandler.process(method);
        }
        this.endpoints = endpointHandler.getEndpoints();
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }
}
