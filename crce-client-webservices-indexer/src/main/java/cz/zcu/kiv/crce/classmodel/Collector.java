package cz.zcu.kiv.crce.classmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class Collector {
    private ClassMap resources;
    private List<Endpoint> endpoints;
    static Logger logger = LogManager.getLogger("endpoints");

    private final String clienClassName =
            "org/springframework/web/reactive/function/client/WebClient";

    private static Collector ourInstance = new Collector();

    public static Collector getInstance() {
        return ourInstance;
    }

    private Collector() {
        this.resources = new ClassMap();
    }

    public ClassMap getClasses() {
        return resources;
    }

    public void addClass(ClassStruct resource) {
        resources.put(resource.getName(), resource);
    }


    private void processClInit(Method method, Map<String, String> constPoolClass) {
        String constVal = null;
        if (method != null) {
            for (Operation operation : method.getOperations()) {


                // LOAD CONSTANTS
                if (operation.getType() == OperationType.STRING_CONSTANT) {
                    constVal = (String) operation.getValue();
                } else if (operation.getOpcode() == Opcodes.PUTSTATIC && constVal != null
                        && operation.getFuncName() != null) {
                    constPoolClass.put(operation.getFuncName(), constVal);
                    constVal = null;
                }

            }
        }
    }

    private void processMethodEndpoint(Method method, Map<String, String> constPoolClass) {

        Endpoint tmpEndpoint = null;
        String constValMethod = null;
        Map<String, String> constPoolMethod = new HashMap<>();

        // Local variables
        for (Operation operation : method.getOperations()) {


            if (operation.getType() == OperationType.STORE && constValMethod != null) {
                constPoolMethod.put("" + operation.getIndex(), constValMethod);
                constValMethod = null;

            }

            // LOAD CONSTANTS
            if (operation.getType() == OperationType.STRING_CONSTANT) {
                constValMethod = (String) operation.getValue();
            } else if (operation.getType() == OperationType.LOAD
                    && constPoolMethod.containsKey(operation.getIndex() + "")) {
                // System.out.println("INDEX=" + operation.getIndex());
                constValMethod = constPoolMethod.get(operation.getIndex() + "");
            } else if (operation.getType() == OperationType.CALL) {
                // DETECT CALL
                if (operation.getDescription().startsWith(this.clienClassName)) {
                    // client request handler

                    if (tmpEndpoint == null) {
                        tmpEndpoint = new Endpoint();
                    }

                    if (operation.getFuncName().equals("get")) {
                        tmpEndpoint.setType(EndpointType.GET);
                    } else if (operation.getFuncName().equals("put")) {
                        tmpEndpoint.setType(EndpointType.PUT);
                    } else if (operation.getFuncName().equals("post")) {
                        tmpEndpoint.setType(EndpointType.POST);
                    } else if (operation.getFuncName().equals("delete")) {
                        tmpEndpoint.setType(EndpointType.DELETE);
                    } else if (operation.getFuncName().equals("uri")) {
                        tmpEndpoint.setUri(constValMethod);
                    } else {
                        // check if there is a return value from call function
                    }

                    constValMethod = null;

                }
            } else if (operation.getOpcode() == Opcodes.GETSTATIC) {
                // get constant from const pool
                constValMethod = constPoolClass.get(operation.getFuncName());
            } else if (operation.getType() == OperationType.RETURN) {
                if (tmpEndpoint != null && tmpEndpoint.getUri() != null) {
                    this.endpoints.add(tmpEndpoint);
                }
                tmpEndpoint = null;
            }

        }
    }

    /*
     * private void processReturnVa
     */
    private void processMethodReturnValue(Method method, Map<String, String> constPoolClass,
            String owner) {
        String constValMethod = "";
        String constAggregationValue = null;
        Map<String, String> constPoolMethod = new HashMap<>();
        /*
         * System.out.println("- " + method.getName()); System.out.println("  * Operations:");
         */

        // Local variables
        for (Operation operation : method.getOperations()) {

            if (operation.getType() == OperationType.STORE) {
                if (constAggregationValue != null) {
                    constValMethod = constAggregationValue;
                    constAggregationValue = null;
                }
                constPoolMethod.put("" + operation.getIndex(), constValMethod);
                constValMethod = "";
                constAggregationValue = null;

            } else if (operation.getType() == OperationType.STRING_CONSTANT) {
                constValMethod = (String) operation.getValue();
            } else if (operation.getType() == OperationType.LOAD
                    && constPoolMethod.containsKey(operation.getIndex() + "")) {
                constValMethod = constPoolMethod.get(operation.getIndex() + "");
            } else if (operation.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && operation.getFuncName().equals("append")) {
                if (constAggregationValue == null) {
                    constAggregationValue = "";
                }
                constAggregationValue += constValMethod;
            } else if (operation.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && operation.getFuncName().equals("toString")) {
                constValMethod = constAggregationValue;
            } else if (operation.getOpcode() == Opcodes.INVOKESTATIC) {
                final String operationOwner = operation.getOwner();
                ClassStruct class_ = this.resources.get(operationOwner);
                if (class_ == null) {
                    continue;
                }
                Method found = class_.getMethod(operation.getFuncName());
                if (found.getReturnValue() == null) {
                    Map<String, String> constPoolClass_ =
                            owner.equals(class_.getName()) ? constPoolClass
                                    : new HashMap<String, String>();
                    this.processMethodReturnValue(found, constPoolClass_, operationOwner);
                }
                constValMethod = found.getReturnValue();
            }

            else if (operation.getType() == OperationType.CALL) {

            } else if (operation.getOpcode() == Opcodes.GETSTATIC) {
                // get constant from const pool
                constValMethod = constPoolClass.get(operation.getFuncName());
            } else if (operation.getType() == OperationType.INT_CONSTANT) {
                constValMethod = operation.getValue() + "";
            } else if (operation.getOpcode() == Opcodes.ARETURN) {
                method.setReturnValue(constValMethod);
            }

        }
    }

    public void process() {
        /*
         * System.out.println("====================================");
         * System.out.println("====================================");
         * System.out.println("PROCESSING CLASS STRUCTS============");
         */

        /*
         * this.endpoints = new ArrayList<>();
         * 
         * Map<String, String> constPoolClass = new HashMap<>();
         * 
         * int i = 0;
         */
        // Processor.processMany(this.resources);
        /*
         * for (ClassStruct class_ : this.resources.values()) { i++;
         * 
         * Method clinit = class_.getClnitMethod(); if (clinit != null) { this.processClInit(clinit,
         * constPoolClass); }
         * 
         * for (Method method : class_.getMethods()) { this.processMethodReturnValue(method,
         * constPoolClass, class_.getName()); }
         * 
         * for (Method method : class_.getMethods()) { this.processMethodEndpoint(method,
         * constPoolClass); } }
         */
        /*
         * for (Endpoint endpoint : endpoints) { logger.debug(endpoint); }
         */
    }
}
