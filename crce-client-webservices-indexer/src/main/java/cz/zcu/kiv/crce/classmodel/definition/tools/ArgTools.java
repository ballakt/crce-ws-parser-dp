package cz.zcu.kiv.crce.classmodel.definition.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.classmodel.definition.ArgDefinitionType;
import cz.zcu.kiv.crce.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;
import cz.zcu.kiv.crce.classmodel.processor.EndpointRequestBody;
import cz.zcu.kiv.crce.classmodel.processor.VarArray;
import cz.zcu.kiv.crce.classmodel.processor.Variable;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.EndpointParameter;

public class ArgTools {

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
    private static String extract(Stack<Variable> values, Set<ArrayList<ArgDefinitionType>> args) {
        String merged = "";
        for (final ArrayList<ArgDefinitionType> oneVersion : args) {
            if (oneVersion.size() == values.size()) {
                for (int i = 0; i < oneVersion.size(); i++) {
                    final Object value = values.remove(0).getValue();
                    if (oneVersion.get(i) == ArgDefinitionType.PATH && isURI(value)) {
                        // TODO: check int arguments if they are valid
                        // TODO: whatabout function with mutliple URIs as argument?
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

    private static Map<String, Object> getParams(Stack<Variable> values,
            Set<ArrayList<ArgDefinitionType>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null) {
            return output;
        }
        for (final ArrayList<ArgDefinitionType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgDefinitionType definition : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + getStringValueVar(var));
                    } else if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(definition.name(), arrayCasted.getInnerArray());
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

    public static void setDataFromArgs(Endpoint endpoint, Stack<Variable> values,
            Set<ArrayList<ArgDefinitionType>> args) {
        if (values.size() == 0 || args == null) {
            return;
        }
        Map<String, Object> params = getParams(values, args);
        final String path = (String) params.getOrDefault(ArgDefinitionType.PATH.name(), null);
        final String baseURL = (String) params.getOrDefault(ArgDefinitionType.BASEURL.name(), null);
        final String httpMethod =
                (String) params.getOrDefault(ArgDefinitionType.HTTPMETHOD.name(), null);
        Object expect = params.getOrDefault(ArgDefinitionType.EXPECT.name(), null);
        Object param = params.getOrDefault(ArgDefinitionType.PARAM.name(), null);
        Object send = params.getOrDefault(ArgDefinitionType.SEND.name(), null);


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
            endpoint.addHttpMethod(HttpMethod.valueOf(httpMethod));
        }
        return;
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgDefinitionType>> args) {
        return extract(values, args);
    }

}
