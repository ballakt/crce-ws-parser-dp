package cz.zcu.kiv.crce.classmodel.definition;

public class DefinitionValuesProcessor {

    public static String processClassName(String className) {
        String convertedClassName = className.replace(".", "/");
        return convertedClassName;
    }
}
