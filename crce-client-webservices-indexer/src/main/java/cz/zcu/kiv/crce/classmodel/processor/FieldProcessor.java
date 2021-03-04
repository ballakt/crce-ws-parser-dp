package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Map;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class FieldProcessor {


    private ClassMap classes;
    private MethodProcessor methodProcessor;
    private Map<String, ConstPool> constPools;

    public void process(ClassMap classes, ClassStruct class_, Map<String, ConstPool> constPools) {
        this.constPools = constPools;
        this.methodProcessor = new MethodProcessor(classes);
        processFields(class_);
    }

    private void processFields(ClassStruct class_) {
        ConstPool constPool = constPools.get(class_.getName());
        String constVal = null;
        Method clinit = class_.getClnitMethod();
        String constAggregationValue = null;
        if (clinit != null) {
            for (Operation operation : clinit.getOperations()) {

                final OperationType type = operation.getType();
                final int opcode = operation.getOpcode();
                String value = (String) operation.getValue();
                final String index = operation.getIndex() + "";
                final String name = operation.getFuncName();

                switch (type) {
                    case STRING_CONSTANT:
                    case INT_CONSTANT:
                        constVal = value;
                    case LOAD:
                        if (!constPool.containsKey(index)) {
                            constVal = "";
                            continue;
                        }
                        constVal = constPool.get(index);
                        break;
                    case CALL:
                        if (Helpers.OpcodeC.isInvokeStatic(opcode)) {
                            final String operationOwner = operation.getOwner();
                            ClassStruct new_class = this.classes.get(operationOwner);
                            if (new_class == null) {
                                continue;
                            }
                            Method found = new_class.getMethod(operation.getFuncName());
                            if (found.getReturnValue() == null) {
                                methodProcessor.process(found, new_class, constPools);
                            }
                            constVal = found.getReturnValue();
                        } else if (Helpers.OpcodeC.isInvokeVirtual(opcode)) {
                            if (Helpers.StringC.isToString(name)) {
                                constVal = constAggregationValue;
                            } else if (Helpers.StringC.isAppend(name)) {
                                if (constAggregationValue == null) {
                                    constAggregationValue = "";
                                }
                                constAggregationValue += constVal;
                            }
                        }
                    case FIELD:
                        if (Helpers.OpcodeC.isPutStatic(opcode)) {
                            if (constAggregationValue != null) {
                                constVal = constAggregationValue;
                            }
                            constPool.put(operation.getFuncName(), constVal);
                            constVal = null;
                            constAggregationValue = null;
                        }
                        // TODO: default fallback on helpers methods
                        break;

                    default:;
                }

                // LOAD CONSTANTS
                if (operation.getType() == OperationType.STRING_CONSTANT) {
                    constVal = (String) operation.getValue();
                } else if (operation.getOpcode() == Opcodes.PUTSTATIC && constVal != null
                        && operation.getFuncName() != null) {
                    constPool.put(operation.getFuncName(), constVal);
                    constVal = null;
                }

            }
        }
    }

}
