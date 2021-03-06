package cz.zcu.kiv.crce.classmodel.processor.tools;

import cz.zcu.kiv.crce.classmodel.processor.Variable;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;

public class VariableTools {
    public static boolean isEmpty(Variable var) {
        return var == null || var.getValue() == null;
    }

    public static boolean isNumberVar(Variable var) {
        return (var.getDescription().equals("I") || var.getDescription().equals("D")
                || var.getDescription().equals("F") || var.getDescription().equals("L")
                || var.getDescription().equals("java/lang/Integer")
                || var.getDescription().equals("java/lang/Double")
                || var.getDescription().equals("java/lang/Long")
                || var.getDescription().equals("java/lang/Float"));
    }

    public static boolean isStringVar(Variable var) {
        return (var.getType() == VariableType.SIMPLE
                || var.getDescription().equals("java/lang/String")
                || var.getOwner().equals("java/lang/StringBuilder"));
    }

}
