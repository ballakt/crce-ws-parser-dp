package cz.zcu.kiv.crce.classmodel.processor.tools;

public class HeaderTools {
    private static final String ACCEPT = "Accept";

    public static boolean isConsumingType(String headerType) {
        return headerType.startsWith(ACCEPT);
    }
}
