package cz.zcu.kiv.crce.classmodel.processor.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassToolsTest {
    private String baseDescription = "org/springframework/web/reactive/function/client/WebClient";
    private String[] baseDescriptionVersions;

    @BeforeEach
    public void setup() {
        String withSemi = baseDescription + ";";
        String withIdent = "L" + baseDescription;
        String withSemiIdent = withIdent + ";";
        final String withArg =
                "(Ljava/lang/String;)Lorg/springframework/web/reactive/function/client/WebClient;";
        final String withEmptyArg =
                "()Lorg/springframework/web/reactive/function/client/WebClient;";
        final String initLike =
                "Lorg/springframework/web/reactive/function/client/WebClient$1.<init>-(Lcom/app/demo/service/ApiService;)V-false;";
        baseDescriptionVersions = new String[] {baseDescription, withSemi, withIdent, withSemiIdent,
                withArg, withEmptyArg, initLike};
    }

    @Test
    public void testDescriptionToOwner() {
        for (String type : baseDescriptionVersions) {
            assertEquals(baseDescription, ClassTools.descriptionToOwner(type));
        }
    }
}
