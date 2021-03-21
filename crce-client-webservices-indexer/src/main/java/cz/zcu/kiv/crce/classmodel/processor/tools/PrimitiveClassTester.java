package cz.zcu.kiv.crce.classmodel.processor.tools;

import java.util.Set;

public class PrimitiveClassTester {
    private static Set<String> primitiveClassNames = Set.of("java/lang/String", "java/lang/Integer",
            "java/lang/Float", "java/lang/Double", "java/lang/Long", "java/lang/Short",
            "java/lang/Character", "java/lang/Byte", "java/lang/Boolean");

    /**
     * Checks if classname is one of the primitive classes like java/lang/String, java/lang/Integer
     * etc.
     * 
     * @param className Classname
     * @return Is class primitive
     */
    public static boolean isPrimitive(String className) {
        return primitiveClassNames.contains(className);
    }
}
