package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.ClassTools;
import cz.zcu.kiv.crce.classmodel.processor.tools.PrimitiveClassTester;
import cz.zcu.kiv.crce.classmodel.processor.tools.VariableTools;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

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
                if (!classPool.containsKey(operation.getFieldName())) {
                    break;
                }
                Variable field = classPool.get(operation.getFieldName());
                if (field == null) {
                    break;
                }
                field.setDescription(ClassTools.descriptionToOwner(operation.getDesc()));

                if (PrimitiveClassTester.isPrimitive(field.getDescription())) {
                    values.add(field);
                }
                /*
                 * values.add(
                 * field.setDescription(ClassTools.descriptionToOwner(operation.getDesc())));
                 */ break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                Variable var = Helpers.StackF.pop(values);
                if (VariableTools.isEmpty(var)) {
                    break;
                }
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
        String newValue = operation.getValue().toString();
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
        if (VariableTools.isEmpty(var)) {
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
        VariablesContainer variables = method.getVariables();
        Variable var = variables.get(operation.getIndex());

        if (var == null) {
            variables.init(operation.getIndex());
            return;
        }
        if (var.getType() != VariableType.OTHER) {
            values.add(var);
        }
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
