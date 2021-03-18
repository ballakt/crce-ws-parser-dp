package cz.zcu.kiv.crce.classmodel.definition;

public class DefinitionValuesProcessor {

    /**
     * Helper function for processing classnames in config. file
     * 
     * @param className Classname
     * @return
     */
    public static String processClassName(String className) {
        String convertedClassName = className.replace(".", "/");
        return convertedClassName;
    }
}
