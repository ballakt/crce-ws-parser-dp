package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.tools.NumTool;
import cz.zcu.kiv.crce.tools.SBTool;

public class AssignmentProcessor {


    protected ClassMap classes;


    public AssignmentProcessor(ClassMap classes) {
        this.classes = classes;
    }

    protected void processFIELD(Operation operation, Stack<StringBuilder> values) {

        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();
        switch (operation.getOpcode()) {
            case Opcodes.GETFIELD:
            case Opcodes.GETSTATIC:
                if (!classPool.containsKey(operation.getFieldName())) {
                    break;
                }
                values.add(new StringBuilder(classPool.get(operation.getFieldName())));
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                if (values.size() == 0) {
                    break;
                }
                StringBuilder sb = values.pop();
                classPool.put(operation.getFieldName(), sb.toString());
                break;
        }

    }

    protected void processCONSTANT(Operation operation, Stack<StringBuilder> values) {
        StringBuilder newValue = new StringBuilder();
        SBTool.set(newValue, operation.getValue());
        values.add(newValue);
    }

    protected void processSTORE(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {
        ConstPool constPool = method.getConstPool();
        final String index = NumTool.numberToString(operation.getIndex());

        /*
         * if (sbAggregated.length() > 0) { constPool.put(index, sbAggregated.toString()); } else if
         * (values.peek() != null) {
         * 
         * } else { return; }
         */
        StringBuilder sb = Helpers.StackF.pop(values);
        if (sb == null) {
            return;
        }
        constPool.put(index, sb.toString());
        // SBTool.clear(sbAggregated);
    }

    protected void processLOAD(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {

        ConstPool constPool = method.getConstPool();
        final String index = NumTool.numberToString(operation.getIndex());

        if (!constPool.containsKey(index)) {
            return;
        }
        values.add(new StringBuilder(constPool.get(index)));
    }

    protected void processInner(MethodWrapper method, Stack<StringBuilder> values) {

        Method methodStruct = method.getMethodStruct();
        for (Operation operation : methodStruct.getOperations()) {
            processOperation(method, operation, values);
        }
    }

    protected void processOperation(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {
        final OperationType type = operation.getType();
        switch (type) {
            case FIELD:
                processFIELD(operation, values);
                break;

            case STRING_CONSTANT:
            case INT_CONSTANT:
                processCONSTANT(operation, values);
                break;

            case LOAD:
                processLOAD(method, operation, values);
                break;

            case STORE:
                processSTORE(method, operation, values);

            case ANEWARRAY:
                // values.poll();
                Helpers.StackF.pop(values);
            default:;

        }
    }
}
