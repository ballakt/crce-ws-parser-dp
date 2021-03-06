package cz.zcu.kiv.crce.classmodel.processor.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MethodToolsTest {
    private static List<Object[]> argsFromSignature;
    private static List<Object[]> methodNameFromSignature;

    @BeforeAll
    public static void init() {
        initGetArgsFromSignature();
        initGetMethodnameFromSignature();
    }

    public static void initGetMethodnameFromSignature() {
        methodNameFromSignature = new LinkedList<>();
        final String oneArg =
                "com/app/demo/service/ApiService$1.<init>(Lcom/app/demo/service/ApiService;)V";
        final String threeArgsWithReturnStmnt =
                "org/springframework/web/client/RestTemplate.postForEntity(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;";
        methodNameFromSignature.add(new String[] {oneArg, "init"});
        methodNameFromSignature.add(new String[] {threeArgsWithReturnStmnt, "postForEntity"});
        methodNameFromSignature.add(
                new String[] {"org/springframework/http/HttpHeaders.<init>-()V-false", "init"});


    }

    public static void initGetArgsFromSignature() {
        argsFromSignature = new LinkedList<>();
        final String oneArg =
                "com/app/demo/service/ApiService$1.<init>(Lcom/app/demo/service/ApiService;)V";
        final String threeArgsWithReturnStmnt =
                "org/springframework/web/client/RestTemplate.postForEntity(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;";
        argsFromSignature
                .add(new Object[] {oneArg, new String[] {"com/app/demo/service/ApiService"}});
        argsFromSignature.add(new Object[] {threeArgsWithReturnStmnt, new String[] {
                "java/lang/String", "java/lang/Object", "java/lang/Class", "java/lang/Object"}});


    }

    @Test
    public void testGetArgsFromSignature() {
        for (Object[] test : argsFromSignature) {
            String signature = (String) test[0];
            String[] expected = (String[]) test[1];
            Set<String> computed = Set.of(MethodTools.getArgsFromSignature(signature));
            for (String expectedArg : expected) {
                if (!computed.contains(expectedArg)) {
                    fail("Missing " + expectedArg);
                }
            }

        }
    }

    @Test
    public void testGetMethodNameFromSignature() {
        for (Object[] test : methodNameFromSignature) {
            String signature = (String) test[0];
            String expected = (String) test[1];
            String methodName = MethodTools.getMethodNameFromSignature(signature);
            assertEquals(expected, methodName);

        }
    }
}
