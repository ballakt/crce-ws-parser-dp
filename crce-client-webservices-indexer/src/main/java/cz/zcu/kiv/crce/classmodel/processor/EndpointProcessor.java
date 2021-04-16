package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.definition.ConfigTools;
import cz.zcu.kiv.crce.classmodel.definition.EDataContainerConfigMap;
import cz.zcu.kiv.crce.classmodel.definition.ApiCallMethodType;
import cz.zcu.kiv.crce.classmodel.definition.EnumConfigMap;
import cz.zcu.kiv.crce.classmodel.definition.EnumFieldOrMethodConfig;
import cz.zcu.kiv.crce.classmodel.definition.ApiCallMethodConfig;
import cz.zcu.kiv.crce.classmodel.definition.MethodConfigMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.VariableTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;

class EndpointHandler extends MethodProcessor {

    private Map<String, Endpoint> endpoints = new HashMap<>();
    private Set<String> typeHolders = ConfigTools.getTypeHolders();
    private EnumConfigMap ed = ConfigTools.getEnumDefinitions();
    private MethodConfigMap md = ConfigTools.getMethodDefinitions();
    private EDataContainerConfigMap eDataConfig = ConfigTools.getEDataContainerConfigMap();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    @Override
    protected void processINVOKESTATIC(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            Variable newEndointData = ArgTools.getEndpointDataFromContainer(values, operation);
            values.push(newEndointData);
            return;
        }
        super.processINVOKESTATIC(values, operation);
    }

    @Override
    protected void processINVOKESPECIAL(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            // TODO: new
            Variable newEndointData = ArgTools.getEndpointDataFromContainer(values, operation);
            mergeVarEndpointData(values, newEndointData);
            return;
        } else if (MethodTools.getType(operation.getDescription()) == MethodType.INIT) {
            removeMethodArgsFromStack(values, operation);
            ClassWrapper class_ = this.classes.getOrDefault(operation.getOwner(), null);
            if (class_ != null && typeHolders.contains(class_.getClassStruct().getParent())
                    && class_.getClassStruct().getSignature() != null) {
                Stack<Object> types = new Stack<>();
                ClassTools.processTypes(class_.getClassStruct().getSignature(), types);
                types.pop(); // remove wraping type
                String type = (String) Helpers.StackF.pop(types);
                // TODO: in future transform to json recursive way
                values.push(new Variable().setDescription(type).setType(VariableType.OTHER));
                return;
            }
        } else {
            removeMethodArgsFromStack(values, operation);
            handleAccessingObject(values, operation);
        }
        super.processINVOKESPECIAL(values, operation);
    }

    @Override
    protected void processINVOKEINTERFACE(Stack<Variable> values, Operation operation) {
        super.processINVOKEINTERFACE(values, operation);
    }

    private static void mergeVarEndpointData(Stack<Variable> values, Variable var) {
        Variable lastVar = Helpers.StackF.peek(values);
        if (!VariableTools.isEmpty(lastVar) && lastVar.getValue() instanceof VarEndpointData) {
            VarEndpointData lastVarEData = (VarEndpointData) lastVar.getValue();
            VarEndpointData varEData = (VarEndpointData) var.getValue();

            lastVarEData.merge(varEData);
        } else {
            values.push(var);
        }
    }

    @Override
    protected void processINVOKEVIRTUAL(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            // TODO: new
            Variable newEndointData = ArgTools.getEndpointDataFromContainer(values, operation);
            mergeVarEndpointData(values, newEndointData);
            return;
        } else {
            super.processINVOKEVIRTUAL(values, operation);
        }
    }

    private void processGENERIC(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
            Operation operation) {
        Variable varEndpoint =
                ArgTools.setDataFromArgs(values, methodDefinition.getArgs(), operation);
        if (varEndpoint != null) {
            Helpers.EndpointF.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }

    }

    /*
     * private void processEDATALIST(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
     * Operation operation) { VarEndpointData eData = new VarEndpointData(); List<VarEndpointData> =
     * Variable var = new Variable(eData).setType(VariableType.ENDPOINTDATA);
     * 
     * 
     * }
     */

    private void processHTTPMetod(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
            ApiCallMethodType type, Operation operation) {
        processGENERIC(values, methodDefinition, operation);
        HttpMethod eType = HttpMethod.values()[type.ordinal()];
        Endpoint endpoint = (Endpoint) Helpers.StackF.peekEndpoint(values).getValue();
        endpoint.addHttpMethod(eType);
    }

    private void processGETFIELDLibEnum(Stack<Variable> values, Operation operation) {
        if (ed.containsKey(operation.getOwner())) {
            HashMap<String, EnumFieldOrMethodConfig> enumClass = ed.get(operation.getOwner());
            if (enumClass.containsKey(operation.getFieldName())) {
                EnumFieldOrMethodConfig enumField = enumClass.get(operation.getFieldName());
                if (enumField.getContentType() != null) {
                    values.push(
                            new Variable(enumField.getContentType()).setType(VariableType.SIMPLE));
                } else if (enumField.getHttpMethod() != null) {
                    values.push(
                            new Variable(enumField.getHttpMethod()).setType(VariableType.SIMPLE));

                } else if (enumField.getHeaderType() != null) {
                    values.push(
                            new Variable(enumField.getHeaderType()).setType(VariableType.SIMPLE));
                }
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

            HashMap<String, ApiCallMethodConfig> methodDefinitionMap = md.get(operation.getOwner());
            if (!methodDefinitionMap.containsKey(operation.getMethodName())) {
                return;
            }
            ApiCallMethodConfig methodDefinition =
                    methodDefinitionMap.get(operation.getMethodName());
            ApiCallMethodType type = methodDefinition.getType();

            switch (type) {
                case INIT:
                    values.push(new Variable().setType(VariableType.ENDPOINT));
                    break;
                case BASEURL:
                case EXECUTE:
                case SEND:
                case EXPECT:
                case PATH:
                case EXCHANGE:
                case GENERIC:
                case CONTENTTYPE:
                case ACCEPT:
                case HEADER:
                    processGENERIC(values, methodDefinition, operation);
                    break;
                default:
                    processHTTPMetod(values, methodDefinition, type, operation);
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
