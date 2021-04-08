package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.EnumDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.EnumFieldOrMethod;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.classmodel.processor.Helpers.StackF;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

class EndpointHandler extends MethodProcessor {

    static Logger logger = LogManager.getLogger("extractor");
    private Map<String, Endpoint> endpoints = new HashMap<>();
    private MethodDefinitionMap md = Definition.getMethodDefinitions();
    private EnumDefinitionMap ed = Definition.getEnumDefinitions();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    private String getClassName(String name) {
        return name.substring(1).replace(";", "");
    }

    private void processEXCHANGE(Stack<Variable> values, MethodDefinition methodDefinition) {
        Stack<Variable> args = Helpers.StackF.removeUntil(VariableType.ENDPOINT, values);

    }

    private void processSEND(Stack<Variable> values, MethodDefinition methodDefinition) {
        processURI(values, methodDefinition);
    }

    private void processEXPECT(Stack<Variable> values, MethodDefinition methodDefinition) {
        processURI(values, methodDefinition);
    }

    private void processURI(Stack<Variable> values, MethodDefinition methodDefinition) {
        Stack<Variable> args = Helpers.StackF.removeUntil(VariableType.ENDPOINT, values);
        Endpoint endpoint = Helpers.StackF.peekEndpoint(values);
        if (endpoint == null) {
            endpoint = new Endpoint();
            values.push(new Variable(endpoint).setType(VariableType.ENDPOINT));
        }
        ArgTools.setDataFromArgs(endpoint, args, methodDefinition.getArgs());
        Helpers.EndpointF.merge(endpoints, endpoint);
    }

    private void processHTTPMetod(Stack<Variable> values, MethodDefinition methodDefinition,
            DefinitionType type) {
        processURI(values, methodDefinition);
        HttpMethod eType = HttpMethod.values()[type.ordinal()];
        Endpoint endpoint = Helpers.StackF.peekEndpoint(values);
        endpoint.addHttpMethod(eType);
    }

    private void processGETFIELDLibEnum(Stack<Variable> values, Operation operation) {
        if (ed.containsKey(operation.getOwner())) {
            HashMap<String, EnumFieldOrMethod> enumClass = ed.get(operation.getOwner());
            if (enumClass.containsKey(operation.getFieldName())) {
                values.push(new Variable(enumClass.get(operation.getFieldName()).getHttpMethod())
                        .setType(VariableType.SIMPLE));
            }
        }
    }

    @Override
    protected void processGETSTATICFIELD(Stack<Variable> values, Operation operation) {
        super.processGETSTATICFIELD(values, operation);
        processGETFIELDLibEnum(values, operation);

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

            // TODO: handle acceptt

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
                case SEND:
                    processSEND(values, methodDefinition);
                    break;
                case EXPECT:
                    processEXPECT(values, methodDefinition);
                    break;
                case PATH:
                    processURI(values, methodDefinition);
                    break;
                case EXCHANGE:
                    processURI(values, methodDefinition);
                    break;
                default:
                    processHTTPMetod(values, methodDefinition, type);
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
