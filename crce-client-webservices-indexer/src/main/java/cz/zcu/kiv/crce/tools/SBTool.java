package cz.zcu.kiv.crce.tools;

public class SBTool {
    public static void clear(StringBuilder sb) {
        sb.setLength(0);
    }

    public static void set(StringBuilder sb, String val) {
        sb.setLength(0); // clear the string
        sb.append(val);
    }

    public static void set(StringBuilder sb, StringBuilder val) {
        sb.setLength(0); // clear the string
        sb.append(val.toString());
    }

    public static void set(StringBuilder sb, Object val) {
        sb.setLength(0); // clear the string
        sb.append(val.toString());
    }

    public static StringBuilder merge(StringBuilder first, StringBuilder second) {
        StringBuilder merged = new StringBuilder();
        merged.append(first.toString());
        merged.append(second.toString());
        return merged;
    }
}
