package cz.zcu.kiv.crce.classmodel.processor.wrappers;

import cz.zcu.kiv.crce.classmodel.processor.VariablesContainer;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class MethodWrapper {
    private Method methodStruct;
    private String description;
    private VariablesContainer vars;


    public MethodWrapper(Method methodStruct) {
        this.methodStruct = methodStruct;
        this.description = ClassTools.descriptionToOwner(methodStruct.getDesc());
        this.vars = new VariablesContainer();
    }

    /**
     * @return the method
     */
    public Method getMethodStruct() {
        return methodStruct;
    }

    public String getDescription() {
        return this.description;
    }

    public VariablesContainer getVariables() {
        return this.vars;
    }


}
