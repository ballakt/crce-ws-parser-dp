package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.definition.ArgDefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.processor.Helpers.StackF;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.PrimitiveClassTester;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

class EndpointHandler extends MethodProcessor {

    static Logger logger = LogManager.getLogger("extractor");
    private Map<String, Endpoint> endpoints = new HashMap<>();
    private MethodDefinitionMap md = Definition.getDefinitions();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    private String getClassName(String name) {
        return name.substring(1).replace(";", "");
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
            DefinitionType type = methodDefinition.getType();


            switch (type) {
                case INIT:
                    values.push(new Variable().setType(VariableType.ENDPOINT));
                    break;
                case BASEURL: {
                    Variable var = values.remove(0);
                    Endpoint endpoint;
                    if (var == null || var.getType() != VariableType.ENDPOINT) {
                        var = new Variable(new Endpoint());
                        values.push(var.setType(VariableType.ENDPOINT));
                    }
                    endpoint = StackF.popEndpoint(values);
                    Variable urlVar = StackF.pop(values);
                    if (urlVar != null) {
                        endpoint.setBaseUrl(urlVar.getValue().toString());
                    }
                }
                    break;
                case EXECUTE: {
                    Endpoint endpoint = Helpers.StackF.peekEndpoint(values);
                    Helpers.EndpointF.merge(endpoints, endpoint);

                }
                    break;
                case EXPECT: {
                    final String className = getClassName(values.pop().getValue().toString());
                    Endpoint endpoint = Helpers.StackF.popEndpoint(values);
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

                }
                    break;
                case URI: {
                    Endpoint endpoint = (Endpoint) values.remove(0).getValue();
                    String val = ArgTools.getURI(values, methodDefinition.getArgs());
                    endpoint.setUri(val != null ? val : null);
                    values.push(new Variable(endpoint).setType(VariableType.ENDPOINT));
                    Helpers.EndpointF.merge(endpoints, endpoint);
                }
                    break;
                default:
                    Endpoint endpoint = Helpers.StackF.removeEndpoint(values, 0);

                    EndpointType eType = EndpointType.values()[type.ordinal()];
                    if (endpoint == null) {
                        endpoint = new Endpoint();
                    }
                    if (values.size() > 0) {
                        ArgTools.setDataFromArgs(endpoint, values, methodDefinition.getArgs());
                    }
                    endpoint.addType(eType);
                    values.add(new Variable(endpoint).setType(VariableType.ENDPOINT));
                    Helpers.EndpointF.merge(endpoints, endpoint);
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
