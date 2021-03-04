package cz.zcu.kiv.crce.classmodel.processor.wrappers;

import cz.zcu.kiv.crce.classmodel.processor.ConstPool;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class MethodWrapper {
    private Method methodStruct;
    private ConstPool constPool;


    public MethodWrapper(Method methodStruct) {
        this.methodStruct = methodStruct;
        constPool = new ConstPool();
    }

    /**
     * @return the method
     */
    public Method getMethodStruct() {
        return methodStruct;
    }

    /**
     * @return the constpool
     */
    public ConstPool getConstPool() {
        return constPool;
    }


}
