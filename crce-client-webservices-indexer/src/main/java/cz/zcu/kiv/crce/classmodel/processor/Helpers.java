package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;

public class Helpers {


    static class StringC {
        static private final String TO_STRING_FC = "toString";
        static private final String APPEND_FC = "append";

        public static boolean isToString(String fName) {
            return fName.equals(TO_STRING_FC);
        }

        public static StringBuilder mergeStringBuilder(StringBuilder first, StringBuilder second) {
            StringBuilder merged = new StringBuilder();
            merged.append(first.toString());
            merged.append(second.toString());
            return merged;
        }

        public static boolean isAppend(String fName) {
            return fName.equals(APPEND_FC);
        }

        enum OperationType {
            APPEND, TOSTRING
        }
    }
    static class OpcodeC {
        public static boolean isPutStatic(int opcode) {
            return opcode == Opcodes.PUTSTATIC;
        }

        public static boolean isGetStatic(int opcode) {
            return opcode == Opcodes.GETSTATIC;
        }

        public static boolean isInvokeStatic(int opcode) {
            return opcode == Opcodes.INVOKESTATIC;
        }

        public static boolean isInvokeVirtual(int opcode) {
            return opcode == Opcodes.INVOKEVIRTUAL;
        }

        public static boolean isReturnA(int opcode) {
            return opcode == Opcodes.ARETURN;
        }
    }

    public static ClassMap convertStructMap(Map<String, ClassStruct> classes) {
        ClassMap converted = new ClassMap();
        for (String key : classes.keySet()) {
            converted.put(key, new ClassWrapper(classes.get(key)));
        }
        return converted;
    }

    static class StackF {
        public static <E> E pop(Stack<E> stack) {
            if (stack.size() > 0) {
                return stack.pop();
            }
            return null;
        }

        public static <E> E peek(Stack<E> stack) {
            if (stack.size() > 0) {
                return stack.peek();
            }
            return null;
        }
    }
    static class EndpointF {
        public static void merge(Map<String, Endpoint> endpoints, Endpoint endpoint) {

            if (endpoints.containsKey(endpoint.getUri())) {
                final Endpoint oldEndpoint = endpoints.get(endpoint.getUri());
                oldEndpoint.getTypes().addAll(endpoint.getTypes());
            } else {
                endpoints.put(endpoint.getUri(), endpoint);
            }
        }

        public static void merge(Map<String, Endpoint> endpoints,
                Map<String, Endpoint> newEndpoints) {
            for (final String key : newEndpoints.keySet()) {
                final Endpoint endpoint = newEndpoints.get(key);
                if (endpoints.containsKey(key)) {
                    merge(endpoints, endpoint);
                } else {
                    endpoints.put(key, endpoint);
                }
            }
        }
    }
}
