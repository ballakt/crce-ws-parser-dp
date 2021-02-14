package cz.zcu.kiv.crce.classmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.classmodel.structures.Field;
import cz.zcu.kiv.crce.classmodel.structures.Method;
import cz.zcu.kiv.crce.classmodel.structures.Operation;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.structures.Operation.OperationType;

public class Collector {
    private Map<String, ClassStruct> resources;
    private List<Endpoint> endpoints;

    private final String clienClassName =
            "org/springframework/web/reactive/function/client/WebClient";

    private static Collector ourInstance = new Collector();

    public static Collector getInstance() {
        return ourInstance;
    }

    private Collector() {
        this.resources = new HashMap<>();
    }

    public Map<String, ClassStruct> getClasses() {
        return resources;
    }

    public void addClass(ClassStruct resource) {
        resources.put(resource.getName(), resource);
    }

    public void process() {
        System.out.println("====================================");
        System.out.println("====================================");
        System.out.println("PROCESSING CLASS STRUCTS============");

        this.endpoints = new ArrayList<>();

        Map<String, String> constPool = new HashMap<>();

        int i = 0;
        String constVal = null;
        for (ClassStruct class_ : this.resources.values()) {
            i++;
            /*
             * System.out.println(i + ". " + class_.getName()); System.out.println("Fields:");
             */
            /*
             * for (Field field : class_.getFields()) { System.out.println("- " + field.getName());
             * }
             */
            /* System.out.println("Methods:"); */
            Method clinit = class_.getClnitMethod();
            if (clinit != null) {
                for (Operation operation : clinit.getBodyLog()) {


                    // LOAD CONSTANTS
                    if (operation.getType() == OperationType.STRING_CONSTANT) {
                        constVal = (String) operation.getValue();
                    } else if (operation.getOpcode() == Opcodes.PUTSTATIC && constVal != null
                            && operation.getName() != null) {
                        constPool.put(operation.getName(), constVal);
                        constVal = null;
                    }

                }
            }


            /*
             * System.out.println("Methods:");
             */ Endpoint tmpEndpoint = null;
            for (Method method : class_.getMethods()) {
                String constValMethod = null;
                Map<String, String> constPoolMethod = new HashMap<>();
                /*
                 * System.out.println("- " + method.getName());
                 * System.out.println("  * Operations:");
                 */

                // Local variables
                for (Operation operation : method.getBodyLog()) {

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

                            if (operation.getName().equals("get")) {
                                /*
                                 * System.out.println("GET");
                                 */ tmpEndpoint.setType(EndpointType.GET);
                            } else if (operation.getName().equals("put")) {
                                /*
                                 * System.out.println("PUT");
                                 */ tmpEndpoint.setType(EndpointType.PUT);
                            } else if (operation.getName().equals("post")) {
                                /*
                                 * System.out.println("POST");
                                 */ tmpEndpoint.setType(EndpointType.POST);
                            } else if (operation.getName().equals("delete")) {
                                tmpEndpoint.setType(EndpointType.DELETE);
                            } else if (operation.getName().equals("uri")) {
                                tmpEndpoint.setUri(constValMethod);
                            }

                            /*
                             * System.out .println("name=" + operation.getName() + " param=" +
                             * constVal);
                             */
                            constValMethod = null;

                        }
                    } else if (operation.getOpcode() == Opcodes.GETSTATIC) {
                        // get constant from const pool
                        constValMethod = constPool.get(operation.getName());
                    } else if (operation.getType() == OperationType.RETURN) {
                        if (tmpEndpoint != null && tmpEndpoint.getUri() != null) {
                            this.endpoints.add(tmpEndpoint);
                        }
                        tmpEndpoint = null;
                    }

                }
            }
        }
        for (Endpoint endpoint : endpoints) {
            System.out.println(endpoint);
        }
    }
}
