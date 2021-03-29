package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.tools.SBTool;

public class BasicProcessor {


    protected ClassMap classes;


    public BasicProcessor(ClassMap classes) {
        this.classes = classes;
    }

    /**
     * Processes operations with fields of concrete class
     * 
     * @param operation Operation performed on field
     * @param values    String values
     */
    protected void processFIELD(Operation operation, Stack<Variable> values) {

        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();
        switch (operation.getOpcode()) {
            case Opcodes.GETFIELD:
            case Opcodes.GETSTATIC:
                if (!classPool.containsKey(operation.getFieldName())
                        || classPool.get(operation.getFieldName()) == null) {
                    break;
                }
                Variable lastVal = Helpers.StackF.peek(values);
                if (lastVal != null) {
                    if (lastVal.getType() == VariableType.OTHER) {
                        values.pop();
                    }
                }
                values.add(classPool.get(operation.getFieldName())
                        .setDescription(ClassTools.descriptionToOwner(operation.getDesc())));
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                // System.out.println("PUTFIELD=" + operation + " index=" + operation.getIndex());
                // TODO: handle webclient variables and store endpoint to its contant/var pool
                if (values.size() == 0) {
                    ClassWrapper class_ = this.classes.get(operation.getOwner());
                    class_.getFieldNames();
                    // classPool.put(operation.getFieldName(), operation.getDesc());
                    break;
                }
                Variable var = values.pop();
                if (var == null || var.getValue() == null) {
                    break;
                }
                // String val = var.getValue().toString();
                classPool.put(operation.getFieldName(), var);
                break;
        }

    }

    /**
     * Processes constants like String, Integer, Float...
     * 
     * @param operation Operation create constant
     * @param values    String values
     */
    protected void processCONSTANT(Operation operation, Stack<Variable> values) {
        StringBuilder newValue = new StringBuilder();
        SBTool.set(newValue, operation.getValue());
        values.add(new Variable(newValue).setType(VariableType.SIMPLE));
    }

    /**
     * Stores values into local variables aka const pool
     * 
     * @param method    Method which includes concrete const pool
     * @param operation Operation for storing into variable
     * @param values    String values
     */
    protected void processSTORE(MethodWrapper method, Operation operation, Stack<Variable> values) {
        // ConstPool constPool = method.getConstPool();
        VariablesContainer variables = method.getVariables();

        Variable var = Helpers.StackF.pop(values);
        if (var == null || var.getValue() == null) {
            return;
        }
        values.removeAll(values);
        variables.set(operation.getIndex(), var);
    }

    /**
     * Loads value from constant pool into values stack
     * 
     * @param method    Method where is Load performed
     * @param operation Loading operation
     * @param values    String values
     */
    protected void processLOAD(MethodWrapper method, Operation operation, Stack<Variable> values) {
        // System.out.println("LOAD=" + operation + " index=" + operation.getIndex());
        VariablesContainer variables = method.getVariables();
        Variable var = variables.get(operation.getIndex());
        // final String index = NumTool.numberToString(operation.getIndex());

        if (var == null) {
            variables.init(operation.getIndex());
            return;
        }
        /*
         * if (!constPool.containsKey(index)) { return; }
         */
        if (var.getType() != VariableType.OTHER)
            values.add(var);
    }

    /**
     * Wrapper for processing all operations of a function
     * 
     * @param method Method which will be processed
     * @param values String values
     */
    protected void processInner(MethodWrapper method, Stack<Variable> values) {

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
            Stack<Variable> values) {
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
