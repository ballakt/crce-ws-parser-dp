package cz.zcu.kiv.crce.classmodel.processor;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

public class NewMethodProcessor extends AssignmentProcessor {

    public NewMethodProcessor(ClassMap classes) {
        super(classes);
    }

    @Override
    protected void processCALL(Operation operation, StringBuilder value,
            StringBuilder aggregatedValue) {

        final String fName = operation.getFuncName();
        final String operationOwner = operation.getOwner();
        final ClassWrapper classWrapper = this.classes.get(operationOwner);


        switch (operation.getOpcode()) {
            case Opcodes.INVOKESTATIC:
                if (classWrapper == null) {
                    return;
                }
                MethodWrapper methodWrapper = classWrapper.getMethod(fName);
                final Method method = methodWrapper.getMethodStruct();
                if (method.getReturnValue() == null) {
                    this.process(methodWrapper);
                }
                setStringBuilderValue(value, method.getReturnValue());
            case Opcodes.INVOKEINTERFACE:
            case Opcodes.INVOKEVIRTUAL:
                if (Helpers.StringC.isToString(fName)) {
                    System.out.println("TOSTRING:" + aggregatedValue);
                    setStringBuilderValue(value, aggregatedValue.toString());
                } else if (Helpers.StringC.isAppend(fName)) {
                    // System.out.println("AGGREGATED:" + aggregatedValue);
                    aggregatedValue.append(value.toString());
                }
                break;
        }

    }

    @Override
    protected void processRETURN(MethodWrapper methodWrapper, Operation operation,
            StringBuilder value, StringBuilder appendedValue) {

        Method method = methodWrapper.getMethodStruct();
        if (appendedValue.length() > 0) {
            this.setStringBuilderValue(value, appendedValue.toString());
        }
        switch (operation.getOpcode()) {
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.IRETURN:
                method.setReturnValue(value.toString());
                break;
            case Opcodes.RETURN:
                method.setReturnValue("");
                break;
        }
        value.setLength(0);
        appendedValue.setLength(0);
    }
}
