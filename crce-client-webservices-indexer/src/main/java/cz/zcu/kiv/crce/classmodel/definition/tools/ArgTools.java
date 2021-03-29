package cz.zcu.kiv.crce.classmodel.definition.tools;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import cz.zcu.kiv.crce.classmodel.definition.ArgDefinitionType;
import cz.zcu.kiv.crce.classmodel.processor.Variable;

public class ArgTools {

    final private String TRUE = "true";
    final private String FALSE = "false";
    final private static Pattern uriPattern = Pattern.compile("(/(\\{?[\\w|\\d]\\}?)+)+");
    final private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    // @source:https://www.baeldung.com/java-check-string-number
    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private boolean isString(String string) {
        if (isNumeric(string)) {
            return false;
        }
        if (string.equals(TRUE) || string.equals(FALSE)) {
            return false;
        }

        return true;
    }

    private static interface ArgValidator {
        boolean isValid(String values);
    }

    private static boolean isURI(String uri) {
        return uriPattern.matcher(uri).matches();
    }


    /**
     * Merges constants put into arguments of a predefined functions
     * 
     * @param values Constants put into arguments
     * @param args   Definition of the methods arguments
     * @return Either merged constants arguments or null
     */
    private static String extract(Stack<Variable> values, Set<ArrayList<ArgDefinitionType>> args,
            ArgValidator validator) {
        String merged = "";
        for (final ArrayList<ArgDefinitionType> oneVersion : args) {
            if (oneVersion.size() == values.size()) {
                for (int i = 0; i < oneVersion.size(); i++) {
                    final Variable value = values.remove(0);
                    if (oneVersion.get(i) == ArgDefinitionType.URI
                            && validator.isValid(value.getValue().toString())) {
                        // TODO: check int arguments if they are valid
                        // TODO: whatabout function with mutliple URIs as argument?
                        merged += value.getValue();
                        return merged;
                    }
                }
                /*
                 * for (final ArgDefinitionType arg : oneVersion) { if (arg ==
                 * ArgDefinitionType.URI) { if (isURI(values.get(i)))break } }
                 */
            }
        }

        return null;
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgDefinitionType>> args) {
        return extract(values, args, (String val) -> isURI(val));
    }

    /*
     * public static StringBuilder getExpectedClass(Stack<StringBuilder> values,
     * Set<ArrayList<ArgDefinitionType>> args) { return extract(values, args, (String val) ->
     * isURI(val)); }
     */
}
