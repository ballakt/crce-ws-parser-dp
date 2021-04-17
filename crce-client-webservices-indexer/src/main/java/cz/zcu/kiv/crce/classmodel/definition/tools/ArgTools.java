package cz.zcu.kiv.crce.classmodel.definition.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.classmodel.definition.ArgConfigType;
import cz.zcu.kiv.crce.classmodel.definition.ConfigTools;
import cz.zcu.kiv.crce.classmodel.definition.EDataContainerConfigMap;
import cz.zcu.kiv.crce.classmodel.definition.EDataContainerMethodConfig;
import cz.zcu.kiv.crce.classmodel.definition.Header;
import cz.zcu.kiv.crce.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;
import cz.zcu.kiv.crce.classmodel.processor.EndpointRequestBody;
import cz.zcu.kiv.crce.classmodel.processor.Helpers;
import cz.zcu.kiv.crce.classmodel.processor.VarArray;
import cz.zcu.kiv.crce.classmodel.processor.VarEndpointData;
import cz.zcu.kiv.crce.classmodel.processor.Variable;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.EndpointParameter;
import cz.zcu.kiv.crce.rest.ParameterCategory;

public class ArgTools {

    private static final String ACCEPT = "Accept";
    private static EDataContainerConfigMap eDataConfig = ConfigTools.getEDataContainerConfigMap();

    private static boolean isURI(Object uri) {
        return (uri instanceof String);
    }

    private static boolean isAcceptance(String headerType) {
        return headerType.startsWith(ACCEPT);
    }

    /**
     * Extracts arguments from args of method
     * 
     * @param values Constants put into arguments
     * @param args Definition of the methods arguments
     * @return Either merged constants arguments or null
     */
    private static String extract(Stack<Variable> values, Set<ArrayList<ArgConfigType>> args) {
        String merged = "";
        for (final ArrayList<ArgConfigType> oneVersion : args) {
            if (oneVersion.size() == values.size()) {
                for (int i = 0; i < oneVersion.size(); i++) {
                    final Object value = values.remove(0).getValue();
                    if (oneVersion.get(i) == ArgConfigType.PATH && isURI(value)) {
                        merged += value;
                        return merged;
                    }
                }
            }
        }

        return null;
    }

    private static String getStringValueVar(Variable var) {
        String val = var.getValue() != null ? var.getValue().toString() : null;
        if (val == null || val.length() == 0) {
            return var.getDescription();
        }
        return val;
    }


    public static Map<String, Object> getParams(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null || values.isEmpty()) {
            return output;
        }
        for (final ArrayList<ArgConfigType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfigType definition : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (definition == ArgConfigType.SKIP) {
                        continue;
                    }
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + getStringValueVar(var));
                    } else if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(definition.name(), arrayCasted.getInnerArray());
                    } else if (val instanceof VarEndpointData) {
                        output.put(definition.name(), val);
                    } else {
                        output.put(definition.name(), getStringValueVar(var));
                    }

                }
            }
        }
        return output;
    }

    private static void addParamToEndpoint(String param, Endpoint endpoint) {
        EndpointParameter newParam = new EndpointParameter();
        newParam.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(param));
        newParam.setDataType(ClassTools.descriptionToOwner(param));
        endpoint.addParameter(newParam);
    }

    private static void addExpectedResponseToEndpoint(String response, Endpoint endpoint) {
        EndpointRequestBody responseBody = new EndpointRequestBody();
        responseBody.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(response));
        response = ClassTools.descriptionToOwner(response);
        responseBody.setStructure(response);
        endpoint.addExpectedResponse(responseBody);
    }

    private static void addRequestBodyToEndpoint(String rBody, Endpoint endpoint) {
        if (rBody == null || rBody.length() == 0) {
            return;
        }
        EndpointRequestBody requestBody = new EndpointRequestBody();
        requestBody.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(rBody));
        rBody = ClassTools.descriptionToOwner(rBody);
        requestBody.setStructure(rBody);
        endpoint.addRequestBody(requestBody);
    }

    private static interface HandleEndpointAttrI {
        public void run(String param, Endpoint endpoint);
    }
    private static interface HandleEndpointPairAttrI {
        public void run(String param1, String param2, Endpoint endpoint);
    }

    private static void handleAttrPair(Object param1, Object param2, Endpoint endpoint,
            HandleEndpointPairAttrI method) {
        if (param1 instanceof String[] && param2 instanceof String[]) {
            String[] params1 = (String[]) param1;
            String[] params2 = (String[]) param2;
            if (params1.length != params2.length) {
                return;
            }
            for (int i = 0; i < params1.length; i++) {
                method.run(params1[i], params2[i], endpoint);
            }
        } else if (param1 instanceof String && param2 instanceof String) {
            method.run((String) param1, (String) param2, endpoint);
        } else if (param1 instanceof String && param2 instanceof String[]) {
            String[] params2 = (String[]) param2;
            for (int i = 0; i < params2.length; i++) {
                method.run((String) param1, params2[i], endpoint);
            }
        }
    }

    private static void handleAttr(Object param, Endpoint endpoint, HandleEndpointAttrI method) {
        if (param instanceof String[]) {
            String[] params = (String[]) param;
            for (String val : params) {
                method.run(val, endpoint);
            }
        } else {
            method.run((String) param, endpoint);
        }

    }

    private static Stack<Variable> methodArgsFromValues(Stack<Variable> values,
            Operation operation) {
        String[] methodArgsDef = MethodTools.getArgsFromSignature(operation.getDescription());
        Stack<Variable> output = new Stack<>();
        if (methodArgsDef == null || methodArgsDef.length == 0 || values.isEmpty()) {
            return output;
        }

        for (int counter = 0; counter < methodArgsDef.length; counter++) {
            output.push(values.pop());
        }
        return output;
    }



    public static Variable getEndpointDataFromContainer(Stack<Variable> values,
            Operation operation) {
        Stack<Variable> args = methodArgsFromValues(values, operation);
        EDataContainerMethodConfig methodConfig = eDataConfig.get(operation.getOwner())
                .get(MethodTools.getMethodNameFromSignature(operation.getDescription()));
        Map<String, Object> params = ArgTools.getParams(args, methodConfig.getArgs());
        VarEndpointData varEndpointData = new VarEndpointData();
        Variable newEndpointData = new Variable(varEndpointData).setType(VariableType.ENDPOINTDATA)
                .setOwner(operation.getOwner());
        return newEndpointData.setValue(setParamsToEndpoint(params, varEndpointData));
    }

    public static Variable setEndpointAttrFromArgs(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> argDefs, Operation operation) {
        Stack<Variable> args = methodArgsFromValues(values, operation);

        Variable varEndpoint = Helpers.StackF.peekEndpoint(values);
        if (varEndpoint == null) {
            Endpoint endpoint = new Endpoint();
            varEndpoint = new Variable(endpoint).setType(VariableType.ENDPOINT);
            values.push(varEndpoint);
        }
        if (args.size() == 0) {
            return varEndpoint;
        }
        Map<String, Object> params = getParams(args, argDefs);
        return varEndpoint.setValue(setParamsToEndpoint(params, (Endpoint) varEndpoint.getValue()));
    }

    private static Endpoint setParamsToEndpoint(Map<String, Object> params, Endpoint endpoint) {
        final String path = (String) params.getOrDefault(ArgConfigType.PATH.name(), null);
        final String baseURL = (String) params.getOrDefault(ArgConfigType.BASEURL.name(), null);
        Object httpMethod = params.getOrDefault(ArgConfigType.HTTPMETHOD.name(), null);
        Object expect = params.getOrDefault(ArgConfigType.EXPECT.name(), null);
        Object param = params.getOrDefault(ArgConfigType.PARAM.name(), null);
        Object send = params.getOrDefault(ArgConfigType.SEND.name(), null);
        Object contentType = params.getOrDefault(ArgConfigType.CONTENTTYPE.name(), null);
        Object headerType = params.getOrDefault(ArgConfigType.HEADERTYPE.name(), null);
        Object headerValue = params.getOrDefault(ArgConfigType.HEADERVALUE.name(), null);
        Object accept = params.getOrDefault(ArgConfigType.ACCEPT.name(), null);
        Object eData = params.getOrDefault(ArgConfigType.EDATA.name(), null);


        if (path != null) {
            endpoint.setPath(path);
        }
        if (baseURL != null) {
            endpoint.setBaseUrl(baseURL);
        }
        if (param != null) {
            handleAttr(param, endpoint, (String p, Endpoint e) -> addParamToEndpoint(p, e));
        }
        if (expect != null) {
            handleAttr(expect, endpoint,
                    (String p, Endpoint e) -> addExpectedResponseToEndpoint(p, e));
        }
        if (send != null) {
            if (send instanceof VarEndpointData) {
                VarEndpointData varEData = (VarEndpointData) send;
                endpoint.merge(varEData);
                varEData.merge(endpoint);
            } else {
                handleAttr(send, endpoint,
                        (String p, Endpoint e) -> addRequestBodyToEndpoint(p, e));
            }
        }
        if (httpMethod != null) {
            handleAttr(httpMethod, endpoint, (String httpmethod, Endpoint e) -> e
                    .addHttpMethod(HttpMethod.valueOf(httpmethod)));
        }
        if (headerType != null && headerValue != null) {
            handleAttrPair(headerType, headerValue, endpoint, (String headerName, String headerVal,
                    Endpoint e) -> e.addHeader(new Header(headerName, headerVal)));
        }
        if (contentType != null) {
            handleAttr(contentType, endpoint, (String cType, Endpoint e) -> e
                    .addProduces(new Header(HeaderTools.CONTENTTYPE, cType)));
        }
        if (accept != null) {
            handleAttr(accept, endpoint, (String aType, Endpoint e) -> e
                    .addConsumes(new Header(HeaderTools.CONTENTTYPE, aType)));
        }
        if (eData != null) {
            // TODO: new
            if (eData instanceof VarEndpointData) {
                VarEndpointData varEData = (VarEndpointData) eData;
                endpoint.merge(varEData);
                varEData.merge(endpoint);
            }
        }
        return endpoint;
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgConfigType>> args) {
        return extract(values, args);
    }

}
