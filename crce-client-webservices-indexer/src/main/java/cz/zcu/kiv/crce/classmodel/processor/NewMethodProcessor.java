package cz.zcu.kiv.crce.classmodel.processor;


import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.Helpers.StringC;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.tools.SBTool;

public class NewMethodProcessor extends AssignmentProcessor {

    private Helpers.StringC.OperationType stringOP = null;

    public NewMethodProcessor(ClassMap classes) {
        super(classes);
    }

    protected void process(MethodWrapper method) {
        Stack<StringBuilder> values = new Stack<>();
        // StringBuilder aggregatedValue = new StringBuilder();

        // this.processInner(method, values, aggregatedValue);
        this.processInner(method, values);
    }

    protected void processCALL(Operation operation, Stack<StringBuilder> values) {

        StringBuilder sb = Helpers.StackF.peek(values);
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
                if (method.getReturnType() == null) {
                    return;
                }
                if (method.getReturnValue() == null) {
                    this.process(methodWrapper);
                }
                SBTool.set(sb, method.getReturnValue());
            case Opcodes.INVOKEINTERFACE:
            case Opcodes.INVOKEVIRTUAL:
                if (Helpers.StringC.isToString(fName)) {
                    // StringBuilder newSb;
                    // newSb = new StringBuilder(appendedValue);
                    // values.add(newSb);
                    this.stringOP = StringC.OperationType.TOSTRING;
                } else if (Helpers.StringC.isAppend(fName)) {
                    /*
                     * values.poll(); if (sb == null) { break; }
                     */
                    if (stringOP == Helpers.StringC.OperationType.APPEND) {
                        final StringBuilder lastVal = Helpers.StackF.pop(values);
                        final StringBuilder befLastVal = Helpers.StackF.pop(values);
                        StringBuilder merged = lastVal;
                        if (befLastVal != null) {
                            merged = SBTool.merge(befLastVal, lastVal);
                        }
                        values.add(merged);
                    }
                    this.stringOP = Helpers.StringC.OperationType.APPEND;
                    // appendedValue.append(sb.toString());
                }
                break;
        }

    }

    protected void processRETURN(MethodWrapper methodWrapper, Operation operation,
            Stack<StringBuilder> values) {
        Method method = methodWrapper.getMethodStruct(); // method.getReturnType()
        if (method.getReturnType() == null) {
            return;
        }
        StringBuilder sb = values.peek();

        switch (operation.getOpcode()) {
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.IRETURN:
                method.setReturnValue(sb.toString());
                break;
            case Opcodes.RETURN:
                method.setReturnValue("");
                break;
        }
        values.removeAll(values);
    }



    @Override
    protected void processOperation(MethodWrapper method, Operation operation,
            Stack<StringBuilder> values) {
        // super.processOperation(method, operation, values, aggregatedValue);
        super.processOperation(method, operation, values);
        final OperationType type = operation.getType();

        switch (type) {
            case RETURN:
                // processRETURN(method, operation, values, aggregatedValue);
                processRETURN(method, operation, values);
                values.removeAll(values);
                break;
            case CALL:
                // processCALL(operation, values, aggregatedValue);
                processCALL(operation, values);
                break;

            default:;
        }
    }

}
