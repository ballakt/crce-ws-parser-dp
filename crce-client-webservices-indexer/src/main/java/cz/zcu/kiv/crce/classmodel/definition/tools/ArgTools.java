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
import cz.zcu.kiv.crce.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;
import cz.zcu.kiv.crce.classmodel.processor.EndpointRequestBody;
import cz.zcu.kiv.crce.classmodel.processor.Helpers;
import cz.zcu.kiv.crce.classmodel.processor.VarArray;
import cz.zcu.kiv.crce.classmodel.processor.VarEndpointData;
import cz.zcu.kiv.crce.classmodel.processor.Variable;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.EndpointParameter;
import cz.zcu.kiv.crce.rest.ParameterCategory;

public class ArgTools {

    private static EDataContainerConfigMap eDataConfig = ConfigTools.getEDataContainerConfigMap();

    private static boolean isURI(Object uri) {
        return (uri instanceof String);
    }


    /**
     * Extracts arguments from args of method
     * 
     * @param values Constants put into arguments
     * @param args   Definition of the methods arguments
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

    private static void varEndpointToParams(VarEndpointData varEndpoint,
            Map<String, Object> params) {
        // TODO: mozna predpokladat ze vsechno muze byt pole??
        final String path = varEndpoint.getPath();
        final String baseUrl = varEndpoint.getBaseUrl();
        final Set<HttpMethod> httpMethod = varEndpoint.getHttpMethods();
        final Set<EndpointRequestBody> expected = varEndpoint.getExpectedResponses();
        final Set<EndpointRequestBody> request = varEndpoint.getRequestBodies();
        final Set<EndpointParameter> eParams = varEndpoint.getParameters();
        final Set<String> contentType = varEndpoint.getProduces();

        if (baseUrl != null) {
            params.put(ArgConfigType.BASEURL.name(), baseUrl);
        }

        if (path != null) {
            params.put(ArgConfigType.PATH.name(), path);
        }

        if (httpMethod != null && httpMethod.size() > 0) {
            params.put(ArgConfigType.HTTPMETHOD.name(), httpMethod);
        }

        if (expected != null && expected.size() > 0) {
            String[] expArray = new String[expected.size()];
            int i = 0;
            for (EndpointRequestBody expItem : expected) {
                expArray[i++] = expItem.getStructure();
            }
            params.put(ArgConfigType.EXPECT.name(), expArray);
        }

        if (request != null && request.size() > 0) {
            String[] reqArray = new String[request.size()];
            int i = 0;
            for (EndpointRequestBody reqItem : request) {
                reqArray[i++] = reqItem.getStructure();
            }
            params.put(ArgConfigType.SEND.name(), reqArray);
        }

        if (eParams != null && eParams.size() > 0) {
            String[] eParamsArray = new String[eParams.size()];
            int i = 0;
            for (EndpointParameter param : eParams) {
                // TODO: ukládat param jako celý objekt -> potřebuju udržet i MATRIX atd...
                eParamsArray[i++] = param.getDataType();
            }
            params.put(ArgConfigType.PARAM.name(), eParamsArray);
        }

        if (contentType != null && contentType.size() > 0) {
            String[] eContentTypeArray = new String[contentType.size()];
            contentType.toArray(eContentTypeArray);
            params.put(ArgConfigType.CONTENTTYPE.name(), eContentTypeArray);
        }

    }

    public static Map<String, Object> getParams(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null) {
            return output;
        }
        for (final ArrayList<ArgConfigType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfigType definition : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + getStringValueVar(var));
                    } else if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(definition.name(), arrayCasted.getInnerArray());
                    } else if (val instanceof VarEndpointData) {
                        varEndpointToParams((VarEndpointData) val, output);
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

    private static void handleEndpointAttrWrapper(Object param, Endpoint endpoint,
            HandleEndpointAttrI method) {
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
        if (methodArgsDef == null || methodArgsDef.length == 0) {
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
        Variable newEndpointData = new Variable(varEndpointData).setType(VariableType.ENDPOINTDATA);
        setParamsToEndpoint(params, varEndpointData);
        return newEndpointData;
    }

    public static Variable setDataFromArgs(Stack<Variable> values,
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
        setParamsToEndpoint(params, (Endpoint) varEndpoint.getValue());
        return varEndpoint;
    }

    private static void setParamsToEndpoint(Map<String, Object> params, Endpoint endpoint) {
        final String path = (String) params.getOrDefault(ArgConfigType.PATH.name(), null);
        final String baseURL = (String) params.getOrDefault(ArgConfigType.BASEURL.name(), null);
        Object httpMethod = params.getOrDefault(ArgConfigType.HTTPMETHOD.name(), null);
        Object expect = params.getOrDefault(ArgConfigType.EXPECT.name(), null);
        Object param = params.getOrDefault(ArgConfigType.PARAM.name(), null);
        Object send = params.getOrDefault(ArgConfigType.SEND.name(), null);
        Object contentType = params.getOrDefault(ArgConfigType.CONTENTTYPE.name(), null);


        if (path != null) {
            endpoint.setPath(path);
        }
        if (baseURL != null) {
            endpoint.setBaseUrl(baseURL);
        }
        if (param != null) {
            handleEndpointAttrWrapper(param, endpoint,
                    (String p, Endpoint e) -> addParamToEndpoint(p, e));
        }
        if (expect != null) {
            handleEndpointAttrWrapper(expect, endpoint,
                    (String p, Endpoint e) -> addExpectedResponseToEndpoint(p, e));
        }
        if (send != null) {
            handleEndpointAttrWrapper(send, endpoint,
                    (String p, Endpoint e) -> addRequestBodyToEndpoint(p, e));
        }
        if (httpMethod != null) {
            handleEndpointAttrWrapper(httpMethod, endpoint, (String httpmethod, Endpoint e) -> e
                    .addHttpMethod(HttpMethod.valueOf(httpmethod)));
            // endpoint.addHttpMethod(HttpMethod.valueOf(httpMethod));
        }
        if (contentType != null) {
            handleEndpointAttrWrapper(contentType, endpoint,
                    (String cType, Endpoint e) -> e.addProduces(cType));
        }
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgConfigType>> args) {
        return extract(values, args);
    }

}
