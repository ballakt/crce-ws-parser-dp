package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.PrimitiveClassTester;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

class EndpointHandler extends MethodProcessor {

    private Stack<Endpoint> endpointsInProgress = new Stack<>();
    static Logger logger = LogManager.getLogger("extractor");
    private Map<String, Endpoint> endpoints = new HashMap<>();
    private MethodDefinitionMap md = Definition.getDefinitions();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    private String getClassName(String name) {
        return name.substring(1).replace(";", "");
    }



    private void noEndpointToProcess(Operation operation) {
        logger.error(
                "Missing endpoint in the endpointsInProgress list (Unwanted state of parser): ["
                        + operation.getMethodName() + "=" + operation.getDesc() + "]");
    }

    /**
     * Processes possible endpoints by detecting method CALL like .uri(), .put() etc.
     * 
     * @param operation Operation to be handled
     * @param values    String values
     */
    @Override
    protected void processCALL(Operation operation, Stack<Variable> values) {

        if (md.containsKey(operation.getOwner())) {

            HashMap<String, MethodDefinition> methodDefinitionMap = md.get(operation.getOwner());
            if (!methodDefinitionMap.containsKey(operation.getMethodName())) {
                return;
            }
            MethodDefinition methodDefinition = methodDefinitionMap.get(operation.getMethodName());

            Set<DefinitionType> typeSet = methodDefinition.getType();
            // TODO: handle sync to body -> aka sending data to endpoint
            if (typeSet.contains(DefinitionType.BASEURL)) {
                Endpoint endpoint = Helpers.StackF.pop(this.endpointsInProgress);
                if (typeSet.contains(DefinitionType.INIT)) {
                    endpoint = new Endpoint();
                    this.endpointsInProgress.push(endpoint);
                } else if (endpoint == null) {
                    noEndpointToProcess(operation);
                    return;
                }
            } else if (typeSet.contains(DefinitionType.INIT)) {
                this.endpointsInProgress.push(new Endpoint());
            } else {
                // TODO: make modules for each type and execute them in order
                if (typeSet.contains(DefinitionType.EXECUTE)) {
                    Endpoint endpoint = (Endpoint) Helpers.StackF.peek(values).getValue();
                    Helpers.EndpointF.merge(endpoints, endpoint);
                } else if (typeSet.contains(DefinitionType.RESPONSE)) {
                    System.out.println("RESPONSE=" + values.peek().getValue());
                    final String className = getClassName(values.pop().getValue().toString());
                    System.out.println("CLASSNAME=" + className);
                    Endpoint endpoint = (Endpoint) Helpers.StackF.pop(values).getValue();
                    if (this.classes.containsKey(className)) {
                        // class exists in the processede JAR
                        endpoint.setExpectedResponse(ClassTools.fieldsToMap(logger, this.classes,
                                this.classes.get(className)));
                    } else if (PrimitiveClassTester.isPrimitive(className)) {
                        endpoint.setExpectedResponse(className);
                    } else {
                        logger.error("Could not find type/class=" + className);
                    }
                    if (endpoint.getUri() == null) {
                        return;
                    }
                    Helpers.EndpointF.merge(endpoints, endpoint);
                } else if (typeSet.contains(DefinitionType.URI)) {
                    Endpoint endpoint = (Endpoint) values.remove(0).getValue();
                    String val = ArgTools.getURI(values, methodDefinition.getArgs());/*  */
                    endpoint.setUri(val != null ? val : null);
                    values.push(new Variable(endpoint));
                } else {
                    Variable lastVar = Helpers.StackF.pop(values);
                    Endpoint endpoint;
                    if (lastVar == null || lastVar.getType() != VariableType.ENDPOINT) {
                        endpoint = new Endpoint();
                        lastVar = new Variable(endpoint).setType(VariableType.ENDPOINT);

                    } else {
                        endpoint = (Endpoint) Helpers.StackF.peek(values).getValue();
                    }

                    // only REST types left, like GET, POST ...
                    DefinitionType[] types = new DefinitionType[typeSet.size()];
                    typeSet.toArray(types);

                    for (final DefinitionType type : types) {
                        endpoint.addType(EndpointType.values()[type.ordinal()]);
                    }
                    values.add(lastVar);
                }
            }
        } else {
            super.processCALL(operation, values);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(MethodWrapper method) {
        // System.out.println("PARAMS=" + method.getMethodStruct().getParameters());
        super.process(method);
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}


public class EndpointProcessor {
    private EndpointHandler endpointHandler;
    private Map<String, Endpoint> endpoints = null;
    private FieldProcessor fieldProcessor;


    public EndpointProcessor(ClassMap classes) {
        this.endpointHandler = new EndpointHandler(classes);
        this.fieldProcessor = new FieldProcessor(classes);
    }

    /**
     * Process class its methods and fields with endpoints handler
     * 
     * @param class_ Class to processing
     */
    public void process(ClassWrapper class_) {
        this.fieldProcessor.process(class_);
        for (MethodWrapper method : class_.getMethods()) {
            endpointHandler.process(method);
        }
        this.endpoints = endpointHandler.getEndpoints();
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}
