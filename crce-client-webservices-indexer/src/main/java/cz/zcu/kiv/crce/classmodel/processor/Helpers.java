package cz.zcu.kiv.crce.classmodel.processor;

import java.util.Map;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;

public class Helpers {


    static class StringC {
        static private final String TO_STRING_FC = "toString";
        static private final String APPEND_FC = "append";

        public static boolean isToString(String fName) {
            return fName.equals(TO_STRING_FC);
        }

        public static boolean isAppend(String fName) {
            return fName.equals(APPEND_FC);
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

}
