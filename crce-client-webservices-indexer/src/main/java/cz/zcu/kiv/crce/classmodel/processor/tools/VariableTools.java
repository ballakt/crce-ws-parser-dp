package cz.zcu.kiv.crce.classmodel.processor.tools;

import cz.zcu.kiv.crce.classmodel.processor.Variable;

public class VariableTools {
    public static boolean isEmpty(Variable var) {
        return var == null || var.getValue() == null;
    }
}
