package cz.zcu.kiv.crce.cli;

import java.io.File;

public class CommandLineInterface {
    private final static String helpShort = "-h";
    private final static String helpLong = "-help";
    private final static String fileShort = "-f";
    private final static String fileLong = "-file";
    private final static String fileLongFull = fileLong + "=";
    private final static String fileShortFull = fileShort + "=";

    /**
     * Retrieves filepath from app arguments
     * 
     * @param args App arguments
     * @return
     */
    private static String getFilePath(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(fileLongFull)) {
                return arg.replace(fileLongFull, "");
            } else if (arg.startsWith(fileShortFull)) {
                return arg.replace(fileShortFull, "");
            }
        }
        return "";
    }

    /**
     * Creates File object from given filepath
     * 
     * @param args App arguments
     * @return File object
     */
    public static File getFile(String[] args) {
        System.out.println(
                " _______ ______  _                                                                    _               ");
        System.out.println(
                "(_______|_____ \\| |                                       _                       _  (_)              ");
        System.out.println(
                " _______ _____) ) |    ____ _____  ____ ___  ____   ___ _| |_  ____ _   _  ____ _| |_ _  ___  ____    ");
        System.out.println(
                "|  ___  |  ____/| |   / ___) ___ |/ ___) _ \\|  _ \\ /___|_   _)/ ___) | | |/ ___|_   _) |/ _ \\|  _ \\   ");
        System.out.println(
                "| |   | | |     | |  | |   | ____( (__| |_| | | | |___ | | |_| |   | |_| ( (___  | |_| | |_| | | | |  ");
        System.out.println(
                "|_|   |_|_|     |_|  |_|   |_____)\\____)___/|_| |_(___/   \\__)_|   |____/ \\____)  \\__)_|\\___/|_| |_|  ");
        System.out.println(
                "====================================================================================================");
        if (args.length == 0
                || (!args[0].startsWith(fileShortFull) && !args[0].startsWith(fileLongFull))) {
            String additionalMSG = "";
            if (args.length > 0 && !args[0].startsWith(helpShort) && !args[0].startsWith(helpLong)) {
                additionalMSG = "Missing the " + fileShort + " or " + fileLong + " param";
            }
            System.out
                    .println("Relative or absolut path to JAR which will be processed: " + fileShort
                            + " | " + fileLong + " = <RelativePathToJAR> | <AbsolutePathToJar>");
            System.out.println("Manual: " + helpShort + " | " + helpLong + "");
            System.err.println(additionalMSG);
            return null;
        }

        return new File(getFilePath(args));
    }
}
