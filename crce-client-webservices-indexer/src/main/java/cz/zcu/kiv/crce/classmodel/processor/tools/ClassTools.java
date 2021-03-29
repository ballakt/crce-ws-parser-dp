package cz.zcu.kiv.crce.classmodel.processor.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassTools {
    private static final String descrOwnerRegexp = "(\\((\\w|\\/|;)*\\)[A-Z])|;";

    public static String descriptionToOwner(String description) {
        String output = description.replaceAll(descrOwnerRegexp, "");
        return output;
    }
}
