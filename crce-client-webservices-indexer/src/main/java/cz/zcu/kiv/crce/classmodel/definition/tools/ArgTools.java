package cz.zcu.kiv.crce.classmodel.definition.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.classmodel.definition.ArgDefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;
import cz.zcu.kiv.crce.classmodel.processor.Variable;

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
                    if (oneVersion.get(i) == ArgDefinitionType.URI && isURI(value)) {
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
    private static Map<String, String> getParams(Stack<Variable> values,
            Set<ArrayList<ArgDefinitionType>> args) {
        Map<String, String> output = new HashMap<>();
        for (final ArrayList<ArgDefinitionType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgDefinitionType definition : versionOfArgs) {
                    final Object val = values.remove(0).getValue();
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + val.toString());
                    } else {
                        output.put(definition.name(), val.toString());
                    }

                }
            }
        }
        return output;
    }

    public static void setDataFromArgs(Endpoint endpoint, Stack<Variable> values,
            Set<ArrayList<ArgDefinitionType>> args) {
        Map<String, String> params = getParams(values, args);
        final String uri = params.getOrDefault(ArgDefinitionType.URI.name(), null);
        final String baseURL = params.getOrDefault(ArgDefinitionType.BASEURL.name(), null);
        // final String param = params.getOrDefault(ArgDefinitionType.PARAM.name(), null);
        endpoint.setUri(uri);
        endpoint.setBaseUrl(baseURL);
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgDefinitionType>> args) {
        return extract(values, args);
    }

}
