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

    /**
     * Processes operations with fields of concrete class
     * 
     * @param operation Operation performed on field
     * @param values    String values
     */
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

    /**
     * Processes constants like String, Integer, Float...
     * 
     * @param operation Operation create constant
     * @param values    String values
     */
    protected void processCONSTANT(Operation operation, Stack<StringBuilder> values) {
        StringBuilder newValue = new StringBuilder();
        SBTool.set(newValue, operation.getValue());
        values.add(newValue);
    }

    /**
     * Stores values into local variables aka const pool
     * 
     * @param method    Method which includes concrete const pool
     * @param operation Operation for storing into variable
     * @param values    String values
     */
    protected void processSTORE(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {
        ConstPool constPool = method.getConstPool();
        final String index = NumTool.numberToString(operation.getIndex());

        StringBuilder sb = Helpers.StackF.pop(values);
        if (sb == null) {
            return;
        }
        constPool.put(index, sb.toString());
    }

    /**
     * Loads value from constant pool into values stack
     * 
     * @param method    Method where is Load performed
     * @param operation Loading operation
     * @param values    String values
     */
    protected void processLOAD(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {

        ConstPool constPool = method.getConstPool();
        final String index = NumTool.numberToString(operation.getIndex());

        if (!constPool.containsKey(index)) {
            return;
        }
        values.add(new StringBuilder(constPool.get(index)));
    }

    /**
     * Wrapper for processing all operations of a function
     * 
     * @param method Method which will be processed
     * @param values String values
     */
    protected void processInner(MethodWrapper method, Stack<StringBuilder> values) {

        Method methodStruct = method.getMethodStruct();
        for (Operation operation : methodStruct.getOperations()) {
            processOperation(method, operation, values);
        }
    }

    /**
     * Processes operation of given method and stores/modifies values holder
     * 
     * @param method    Method where is operation performed
     * @param operation Concrete operation
     * @param values    String values
     */
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
                Helpers.StackF.pop(values);
            default:;

        }
    }
}
