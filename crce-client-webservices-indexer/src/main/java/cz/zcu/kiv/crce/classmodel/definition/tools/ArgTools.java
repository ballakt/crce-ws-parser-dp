package cz.zcu.kiv.crce.classmodel.definition.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import cz.zcu.kiv.crce.classmodel.definition.ArgDefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;

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

    private static boolean isURI(String uri) {
        return uriPattern.matcher(uri).matches();
    }

    public static StringBuilder merge(Stack<StringBuilder> values,
            Set<ArrayList<ArgDefinitionType>> args) {
        StringBuilder merged = new StringBuilder();
        for (final ArrayList<ArgDefinitionType> oneVersion : args) {
            if (oneVersion.size() == values.size()) {
                for (int i = 0; i < oneVersion.size(); i++) {
                    final StringBuilder value = values.remove(0);
                    if (oneVersion.get(i) == ArgDefinitionType.URI && isURI(value.toString())) {
                        // TODO: handler int arguments if they are valid
                        merged.append(value);
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
}
