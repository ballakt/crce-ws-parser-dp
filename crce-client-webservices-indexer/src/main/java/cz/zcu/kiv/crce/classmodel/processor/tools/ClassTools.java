package cz.zcu.kiv.crce.classmodel.processor.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Field;

public class ClassTools {
    private static final String descrOwnerRegexp = "(\\((\\w|\\/|;)*\\)[A-Z])|;";
    private static final String methodSetGetPrefixRegExp = "^(set)";

    private static String methodNameIntoFieldName(String methodName) {
        String newMethodName = methodName.replaceFirst(methodSetGetPrefixRegExp, "");
        return newMethodName.replaceFirst(newMethodName.charAt(0) + "",
                Character.toLowerCase(newMethodName.charAt(0)) + "");
    }

    public static String descriptionToOwner(String description) {
        String output = description.replaceAll(descrOwnerRegexp, "");
        return output;
    }

    /**
     * Converts fields of class into map structure like "field_name: field_type"
     * 
     * @param class_ Input class
     * @return Map of fields
     */
    public static Map<String, Object> fieldsToMap(Logger logger, ClassMap classes,
            ClassWrapper class_) {
        Map<String, Object> map = new HashMap<>();
        final List<MethodWrapper> methods = class_.getMethods();
        final Map<String, Field> fields = class_.getFieldsContainer();

        for (final MethodWrapper method : methods) {
            final String expFieldName = methodNameIntoFieldName(method.getMethodStruct().getName());
            if (fields.containsKey(expFieldName)) {
                // checks the field has getter and setter function
                final Field field = fields.get(expFieldName);
                final String fieldName = field.getName();
                final String fieldType = field.getDataType().getBasicType();

                if (PrimitiveClassTester.isPrimitive(fieldType)) {
                    map.put(fieldName, field.getDataType().getBasicType());
                } else if (classes.containsKey(fieldType)) {
                    ClassWrapper classWrapper = classes.get(fieldType);
                    map.put(fieldName, fieldsToMap(logger, classes, classWrapper));
                } else {
                    logger.error(
                            "Could not find type/class=" + fieldType + "of this field=" + field);
                }

            }
        }
        return map;
    }
}
