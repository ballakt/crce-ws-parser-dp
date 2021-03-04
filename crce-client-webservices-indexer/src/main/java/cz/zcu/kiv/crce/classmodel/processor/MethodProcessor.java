package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import cz.zcu.kiv.crce.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class MethodProcessor {

    private ClassMap classes;
    private Map<String, Method> methods;
    private Map<String, ConstPool> constPools;

    /**
     * 
     */
    public MethodProcessor(ClassMap classes) {
        this.methods = new HashMap<>();
        this.classes = classes;
    }

    public void process(Method method, ClassStruct classScope, Map<String, ConstPool> constPools) {
        this.constPools = constPools;
        setReturnValue(method, classScope);
        methods.put(method.getName(), method);
    }

    private void setReturnValue(Method method, ClassStruct classScope) {
        ConstPool constPoolClass;
        String constValMethod = "";
        String constAggregationValue = null;
        final String className = classScope.getName();



        if (!constPools.containsKey(className)) {
            constPools.put(className, new ConstPool());
        }

        constPoolClass = constPools.get(className);
        ConstPool constPoolMethod = new ConstPool();

        for (Operation operation : method.getOperations()) {

            final OperationType type = operation.getType();
            final int opcode = operation.getOpcode();
            final String index = operation.getIndex() + "";
            String value = (String) operation.getValue();
            final String name = operation.getFuncName();

            switch (type) {
                case STORE:
                    constValMethod =
                            constAggregationValue != null ? constAggregationValue : constValMethod;
                    constPoolMethod.put(index, constValMethod);
                    constValMethod = null;
                    constAggregationValue = null;
                    break;

                case FIELD:
                    if (Helpers.OpcodeC.isGetStatic(opcode)) {
                        constValMethod = constPoolClass.get(operation.getFuncName());
                    }
                    break;
                case STRING_CONSTANT:
                    constValMethod = value;
                case INT_CONSTANT:
                    constValMethod = value;
                    break;
                case LOAD:
                    if (!constPoolMethod.containsKey(index)) {
                        // System.err.println("Const pool does not contain a key=" + index);
                        constValMethod = "";
                        continue;
                    }
                    constValMethod = constPoolMethod.get(index);
                    break;

                case CALL:
                    if (Helpers.OpcodeC.isInvokeStatic(opcode)) {
                        final String operationOwner = operation.getOwner();
                        ClassStruct class_ = this.classes.get(operationOwner);
                        if (class_ == null) {
                            continue;
                        }
                        Method found = class_.getMethod(operation.getFuncName());
                        if (found.getReturnValue() == null) {
                            this.setReturnValue(found, class_);
                        }
                        constValMethod = found.getReturnValue();
                    } else if (Helpers.OpcodeC.isInvokeVirtual(opcode)) {
                        if (Helpers.StringC.isToString(name)) {
                            constValMethod = constAggregationValue;
                        } else if (Helpers.StringC.isAppend(name)) {
                            if (constAggregationValue == null) {
                                constAggregationValue = "";
                            }
                            constAggregationValue += constValMethod;
                        }
                    }
                    break;

                case RETURN:
                    if (Helpers.OpcodeC.isReturnA(opcode)) {
                        method.setReturnValue(constValMethod);
                    }
                    break;
                default:;

            }
        }
    }

    public String getReturnValue(Method method) {
        return null;
    }

}
