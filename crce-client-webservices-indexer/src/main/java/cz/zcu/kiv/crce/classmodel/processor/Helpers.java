package cz.zcu.kiv.crce.classmodel.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;

public class Helpers {


    static class StringC {
        static private final String APPEND_FC = "append";
        static private final String TO_STRING_FC = "toString";

        /**
         * Detects a toString method by its name
         * 
         * @param name name of the method
         * @return is or is not
         */
        public static boolean isToString(String name) {
            return name.equals(TO_STRING_FC);
        }

        /**
         * Merges two StringBuilders into newone
         * 
         * @param first  First StringBuilder
         * @param second Second StringBuilder
         * @return Merged StringBuilder
         */
        public static StringBuilder mergeStringBuilder(StringBuilder first, StringBuilder second) {
            StringBuilder merged = new StringBuilder();
            merged.append(first.toString());
            merged.append(second.toString());
            return merged;
        }

        /**
         * Detects an append method by its name
         * 
         * @param name name of the method
         * @return is or is not
         */
        public static boolean isAppend(String name) {
            return name.equals(APPEND_FC);
        }

        enum OperationType {
            APPEND, TOSTRING
        }
    }
    static class OpcodeC {
        /**
         * Detects a putstatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isPutStatic(int opcode) {
            return opcode == Opcodes.PUTSTATIC;
        }

        /**
         * Detects a getstatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isGetStatic(int opcode) {
            return opcode == Opcodes.GETSTATIC;
        }

        /**
         * Detects an invokestatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isInvokeStatic(int opcode) {
            return opcode == Opcodes.INVOKESTATIC;
        }

        /**
         * Detects an invokevirtual operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isInvokeVirtual(int opcode) {
            return opcode == Opcodes.INVOKEVIRTUAL;
        }

        /**
         * Detects a return operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isReturnA(int opcode) {
            return opcode == Opcodes.ARETURN;
        }
    }

    /**
     * Wrappes class structure with additional fields and functions
     * 
     * @param classes Map of classes
     * @return Wrapped classes
     */
    public static ClassMap convertStructMap(Map<String, ClassStruct> classes) {
        ClassMap converted = new ClassMap();
        for (String key : classes.keySet()) {
            converted.put(key, new ClassWrapper(classes.get(key)));
        }
        return converted;
    }

    static class StackF {
        /**
         * Handles poping of an empty stack
         * 
         * @param <E>   Type of values in stack
         * @param stack Stack
         * @return value or null if stack is empty
         */
        public static <E> E pop(Stack<E> stack) {
            if (stack.size() > 0) {
                return stack.pop();
            }
            return null;
        }

        public static Endpoint popEndpoint(Stack<Variable> stack) {
            Variable var = pop(stack);
            if (var != null && var.getType() == VariableType.ENDPOINT) {
                return (Endpoint) var.getValue();
            }
            return null;
        }

        /**
         * Handles peek of an empty stack
         * 
         * @param <E>   Type of values in stack
         * @param stack Stack
         * @return value or null if stack is empty
         */
        public static <E> E peek(Stack<E> stack) {
            if (stack.size() > 0) {
                return stack.peek();
            }
            return null;
        }

        // public static <E> constains(Stack<>)

        /*
         * private static boolean isType(Object object, Class<?> type) { return (object != null &&
         * object.getClass() == type); }
         */

        public static boolean contains(Stack<Variable> stack, VariableType vType) {
            for (Variable var : stack) {
                System.out.println("VAR=" + var.getType());
                System.out.println("VTYPE=" + vType);
                if (var.getType() == vType) {
                    return true;
                }
            }
            return false;
        }

        public static Endpoint peekEndpoint(Stack<Variable> stack) {
            Variable var = peek(stack);
            if (var != null && var.getType() == VariableType.ENDPOINT) {
                return (Endpoint) var.getValue();
            }
            return null;
        }

        public static Endpoint removeEndpoint(Stack<Variable> stack, int position) {
            Variable var = null;
            if (stack.size() < position) {
                var = stack.get(position);
            }
            if (var != null && var.getType() == VariableType.ENDPOINT) {
                return (Endpoint) stack.remove(position).getValue();
            }
            return null;
        }

        public static Stack<Variable> removeUntil(VariableType type, Stack<Variable> stack) {
            Stack<Variable> vars = new Stack<>();
            /*
             * if (!contains(stack, type)) { return vars; }
             */
            Variable it = peek(stack);
            for (; it != null && it.getType() != VariableType.ENDPOINT; it = peek(stack)) {
                vars.push(it);
                pop(stack);
            }
            return vars;
        }
    }
    static class EndpointF {
        /**
         * Merges new endpoints into existing map of endpoints
         * 
         * @param endpoints Map of endpoints
         * @param endpoint  New endpoint
         */
        public static void merge(Map<String, Endpoint> endpoints, Endpoint endpoint) {
            if (endpoint.getPath() == null) {
                return;
            }
            if (endpoints.containsKey(endpoint.getPath())) {
                final Endpoint oldEndpoint = endpoints.get(endpoint.getPath());
                oldEndpoint.merge(endpoint);
            } else {
                endpoints.put(endpoint.getPath(), endpoint);
            }
        }

        /**
         * Merges new endpoints map into existing one
         * 
         * @param endpoints    Current endpoints
         * @param newEndpoints New endpoints
         */
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
    /*
     * static class SetF { public static <E> boolean equals(Set<E> first, Set<E> second) { if
     * (first.size() != second.size()) { return false; }
     * 
     * return false; } }
     */
}
