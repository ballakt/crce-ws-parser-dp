package cz.zcu.kiv.crce.classmodel.processor.wrappers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import cz.zcu.kiv.crce.classmodel.processor.ConstPool;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class ClassWrapper {
    private ConstPool classPool;
    private List<MethodWrapper> methodsList;
    private ClassStruct classStruct;
    private Map<String, MethodWrapper> methods;

    public ClassWrapper(ClassStruct classStruct) {
        this.classStruct = classStruct;
        classPool = new ConstPool();
        methods = new HashMap<>();
        for (Method method : classStruct.getMethods()) {
            methods.put(method.getName(), new MethodWrapper(method));
        }
    }

    /**
     * @return the classPool
     */
    public ConstPool getClassPool() {
        return classPool;
    }

    /**
     * @return the classStruct
     */
    public ClassStruct getClassStruct() {
        return classStruct;
    }

    public MethodWrapper getMethod(String name) {
        return this.methods.get(name);
    }

    public List<MethodWrapper> getMethods() {
        if (methodsList == null) {
            methodsList = new LinkedList<MethodWrapper>(methods.values());
        }
        return methodsList;
    }

    public void removeMethod(String name) {
        this.methods.remove(name);
        methodsList = new LinkedList<MethodWrapper>(methods.values());
    }
}
