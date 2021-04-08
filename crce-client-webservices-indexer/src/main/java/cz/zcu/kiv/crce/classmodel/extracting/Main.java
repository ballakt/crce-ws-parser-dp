package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cz.zcu.kiv.crce.classmodel.processor.Processor;


public class Main {

    /*
     * private static List<Object> getTypes(String signature) { Stack<Object> typesStack = new
     * Stack<>(); processTypes(signature, typesStack); List<Object> types = new
     * LinkedList<>(typesStack); Collections.reverse(types); return types; }
     * 
     * private static void processTypes(String signature, Stack<Object> types) {
     * 
     * String baseTypeRegex = "[BCDFIJSZ]"; Pattern baseTypePattern =
     * Pattern.compile(baseTypeRegex); String objectTypeRegex = "^L[^;<>]+(<.*>)*;"; Pattern
     * objectTypePattern = Pattern.compile(objectTypeRegex);
     * 
     * Matcher matcher = baseTypePattern.matcher(signature); String dateType;
     * 
     * if (matcher.find() && matcher.start() == 0) { dateType = signature.substring(0,
     * matcher.end()); types.push(dateType); return; }
     * 
     * matcher = objectTypePattern.matcher(signature);
     * 
     * if (matcher.find() && matcher.start() == 0) { signature = signature.substring(0,
     * matcher.end()); if (!signature.contains("<")) { types.push(signature.replaceFirst("L",
     * "").replace(";", "")); } else { String innerType = signature.replaceFirst(".*<(.*?)>.*",
     * "$1"); if (innerType != null) { String[] params = innerType.split(";"); if (params.length >
     * 1) { types.push(params); } else { types.push(innerType.replaceFirst("L", "")); } String
     * outerType = signature.replaceFirst("<" + innerType + ">", ""); processTypes(outerType,
     * types); } } } }
     */

    public static void main(String[] args) throws Exception {
        /*
         * String signature = "Ljava/lang/String;"; List<Object> types = getTypes(signature);
         * 
         * for (Object type : types) { System.out.println("TYPE=" + type.toString()); }
         */

        File jarFile = new File(
                "/home/anonym/projects/crce-ws-parser-dp/crce-client-webservices-indexer/src/test/resources/spring_resttemplate.jar");

        /*
         * File jarFile = CommandLineInterface.getFile(args);
         * 
         * if (jarFile == null) { return; }
         */

        Processor.process(jarFile);

    }
}
