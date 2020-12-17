package cz.zcu.kiv.crce.tools;

import java.io.File;

public final class FileTools {

    private final static char DOT = '.';
    private final static String EMPTY_STRING = "";

    /**
     * Extracts extension from file
     * 
     * @param file
     * @return extension of fileName
     */
    public static String getExtension(File file) {
        final String fileName = file.toString();
        return getExtension(fileName);
    }

    /**
     * Extracts extension from filename
     * 
     * @param fileName
     * @return extension of fileName
     */
    public static String getExtension(String fileName) {
        final int indexOfDot = fileName.indexOf(DOT);
        if (indexOfDot == 0) {
            return EMPTY_STRING;
        }
        return fileName.substring(indexOfDot + 1, fileName.length());
    }
}
