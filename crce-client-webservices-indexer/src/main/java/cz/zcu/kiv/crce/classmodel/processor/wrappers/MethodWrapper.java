package cz.zcu.kiv.crce.classmodel.processor.wrappers;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.VariablesContainer;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class MethodWrapper {
    private Method methodStruct;
    private String description;
    private VariablesContainer vars;
    private boolean isStatic = false;


    public MethodWrapper(Method methodStruct, String owner) {
        this.methodStruct = methodStruct;
        this.description = ClassTools.descriptionToOwner(methodStruct.getDesc());
        this.isStatic = (methodStruct.getAccess() & Opcodes.ACC_STATIC) != 0;
        if (isStatic) {
            this.vars = new VariablesContainer(methodStruct.getParameters());
        } else {
            this.vars = new VariablesContainer(methodStruct.getParameters(), owner);
        }
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
