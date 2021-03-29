package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Iterator;
import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.Helpers.StringC;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.tools.PrimitiveClassTester;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class MethodProcessor extends BasicProcessor {

    private Helpers.StringC.OperationType stringOP = null;

    public MethodProcessor(ClassMap classes) {
        super(classes);
    }

    /**
     * Process method its Strings and numbers
     * 
     * @param method Method to process
     */
    protected void process(MethodWrapper method) {
        Stack<Variable> values = new Stack<>();
        this.processInner(method, values);
    }


    /**
     * Process CALL operation (toString, append - operation with Strings)
     * 
     * @param operation Operation to be handled
     * @param values    String values
     */
    protected void processCALL(Operation operation, Stack<Variable> values) {

        final String fName = operation.getFuncName();
        final String operationOwner = operation.getOwner();
        final ClassWrapper classWrapper = this.classes.get(operationOwner);


        switch (operation.getOpcode()) {
            case Opcodes.INVOKESTATIC:
                if (classWrapper == null) {
                    break;
                }
                MethodWrapper methodWrapper = classWrapper.getMethod(fName);
                final Method method = methodWrapper.getMethodStruct();
                if (methodWrapper.getDescription().equals("")) {
                    return;
                }
                if (method.getReturnValue() == null) {
                    this.process(methodWrapper);
                }
                Variable newVar = new Variable();
                // TODO: check if var is simple
                if (PrimitiveClassTester.isPrimitive(methodWrapper.getDescription())) {
                    newVar.setType(VariableType.SIMPLE);
                } else {
                    newVar.setType(VariableType.OTHER);
                }
                newVar.setValue(method.getReturnValue());
                values.push(newVar);
                break;
            // SBTool.set(sb, method.getReturnValue());
            // case Opcodes.INVOKEINTERFACE:
            case Opcodes.INVOKEVIRTUAL:
                if (Helpers.StringC.isToString(fName)) {
                    this.stringOP = StringC.OperationType.TOSTRING;
                } else if (Helpers.StringC.isAppend(fName)) {
                    if (this.stringOP == Helpers.StringC.OperationType.APPEND) {

                        Iterator valuesIterator = values.iterator();
                        Variable merged = new Variable().setType(VariableType.SIMPLE);
                        while (valuesIterator.hasNext()) {
                            Variable nextVar = (Variable) valuesIterator.next();
                            if (nextVar.getValue() == null) {
                                continue;
                            }
                            if (nextVar.getType() == VariableType.SIMPLE) {
                                merged.add(nextVar.getValue().toString());
                            }
                        }

                        /*
                         * for (Variable test : values) { System.out.println(test.getValue()); }
                         */

                        // APPEND was a previous operation
                        // final StringBuilder lastVal = Helpers.StackF.pop(values);
                        // final Variable lastVal = Helpers.StackF.pop(values);
                        // final Variable befLastVal = Helpers.StackF.pop(values);
                        // final StringBuilder befLastVal = Helpers.StackF.pop(values);
                        // StringBuilder merged = lastVal;
                        /*
                         * Variable merged = befLastVal; if (merged != null && merged.getValue() !=
                         * null) { merged.add(lastVal); }
                         */
                        values.removeAll(values);
                        values.add(merged);
                    }
                    this.stringOP = Helpers.StringC.OperationType.APPEND;
                } else {
                    if (!this.classes.containsKey(operation.getOwner())) {
                        break;
                    }
                    ClassWrapper cw = this.classes.get(operation.getOwner());
                    MethodWrapper mw = cw.getMethod(operation.getFuncName());
                    if (mw == null) {
                        break;
                    }
                    Variable variable = new Variable(mw.getMethodStruct().getReturnValue())
                            .setType(VariableType.OTHER).setDescription(mw.getDescription());

                    if (PrimitiveClassTester.isPrimitive(mw.getDescription())) {
                        variable.setType(VariableType.SIMPLE);
                    }
                    values.add(variable);
                }
                break;
            case Opcodes.INVOKESPECIAL:
                Variable variable = Helpers.StackF.peek(values);

                if (variable == null) {
                    variable = new Variable().setType(VariableType.OTHER)
                            .setValue(operation.getDescription());
                    values.add(variable);
                    return;
                }
                if (variable.getType() != VariableType.SIMPLE) {
                    variable.setValue(operation.getDescription());
                }
                // TODO: save new instances to variable -> if not implemented may lead to missread
                // of endpoints
                // System.out.println("INVOKE_SPECIAL=" + operation);

        }

    }

    /**
     * Processes return values of each method and sets its return value
     * 
     * @param methodWrapper Method to be processed
     * @param operation     Explicit operation
     * @param values        String values
     */
    protected void processRETURN(MethodWrapper methodWrapper, Operation operation,
            Stack<Variable> values) {
        Method method = methodWrapper.getMethodStruct();

        if (method.getDesc().equals("()V")) {
            return;
        }
        if (values.size() == 0) {
            return;
        }
        Variable var = values.peek();
        switch (operation.getOpcode()) {
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.IRETURN:
                method.setReturnValue(var.getValue().toString());
                break;
            case Opcodes.RETURN:
                method.setReturnValue("");
                break;
        }
        values.removeAll(values);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void processOperation(MethodWrapper method, Operation operation,
            Stack<Variable> values) {
        super.processOperation(method, operation, values);
        final OperationType type = operation.getType();
        switch (type) {
            case RETURN:
                processRETURN(method, operation, values);
                values.removeAll(values);
                break;
            case CALL:
                processCALL(operation, values);
                break;

            default:;
        }
    }

}
