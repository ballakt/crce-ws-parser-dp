package cz.zcu.kiv.crce.classmodel.processor;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class AssignmentProcessor {


    protected ClassMap classes;


    public AssignmentProcessor(ClassMap classes) {
        this.classes = classes;
    }

    protected void setStringBuilderValue(StringBuilder sb, String val) {
        sb.setLength(0); // clear the string
        sb.append(val);
    }

    protected void processFIELD(Operation operation, StringBuilder sb) {
        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();
        switch (operation.getOpcode()) {
            case Opcodes.GETFIELD:
            case Opcodes.GETSTATIC:
                // System.out.println("GETSTATIC=" + operation.getFieldName() + " VAL="+
                // classPool.get(operation.getFieldName()));
                setStringBuilderValue(sb, classPool.get(operation.getFieldName()));
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                classPool.put(operation.getFieldName(), sb.toString());
                sb.setLength(0);
                break;
        }

    }

    protected void processCALL(Operation operation, StringBuilder value,
            StringBuilder aggregationValue) {
    }

    protected void processRETURN(MethodWrapper methodWrapper, Operation operation,
            StringBuilder value, StringBuilder appendedValue) {
    }

    protected void processCONSTANT(Operation operation, StringBuilder value) {
        final Object rawValue = operation.getValue();
        setStringBuilderValue(value, rawValue.toString());
    }

    protected void processSTORE(MethodWrapper method, Operation operation, StringBuilder sb,
            StringBuilder sbAggregated) {
        ConstPool constPool = method.getConstPool();
        final String index = operation.getIndex() + "";
        // System.out.println("METHOD=" + method.getMethodStruct() + " STORE=" + sb + ", STORE="+
        // sbAggregated + " INDEX=" + index);
        if (sbAggregated.length() > 0) {
            constPool.put(index, sbAggregated.toString());
        } else {
            constPool.put(index, sb.toString());
        }
        sb.setLength(0);
        sbAggregated.setLength(0);
    }

    protected void processLOAD(MethodWrapper method, Operation operation, StringBuilder sb) {
        String value = "";
        ConstPool constPool = method.getConstPool();
        final String index = operation.getIndex() + "";

        // System.out .println("METHOD=" + method.getMethodStruct() + " LOAD=" + sb + " INDEX=" +
        // index);
        if (!constPool.containsKey(index)) {
            // System.err.println("Const pool does not contain a key=" + index);
            return;
        }
        value = constPool.get(index);
        this.setStringBuilderValue(sb, value);
    }

    public void process(MethodWrapper method) {
        StringBuilder value = new StringBuilder();
        StringBuilder valueAggergated = new StringBuilder();
        Method methodStruct = method.getMethodStruct();

        for (Operation operation : methodStruct.getOperations()) {
            final OperationType type = operation.getType();
            // System.out.println("VALUE=" + valProcess.toString());
            // System.out.println("AGGREGATED_VALUE=" + valProcessAggregated.toString());
            switch (type) {
                case FIELD:
                    processFIELD(operation, value);
                    break;

                case STRING_CONSTANT:
                case INT_CONSTANT:
                    processCONSTANT(operation, value);
                    break;

                case LOAD:
                    processLOAD(method, operation, value);
                    break;

                case STORE:
                    processSTORE(method, operation, value, valueAggergated);

                case CALL:
                    processCALL(operation, value, valueAggergated);
                    break;

                case RETURN:
                    processRETURN(method, operation, value, valueAggergated);
                    break;

                default:;

            }
        }
    }
}
