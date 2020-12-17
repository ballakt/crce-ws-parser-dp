package cz.zcu.kiv.crce.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class FileToolsTest {
    @Test
    public void testGetExtension() {
        final HashMap<String, String> fileNames =
                new HashMap<String, String>(Map.of("filename.java", "java", "file_name.c", "c",
                        "fileName.java", "java", "File_Name.cpp", "cpp"));

        for (final String fileName : fileNames.keySet()) {
            final String extension = FileTools.getExtension(fileName);
            assertEquals(fileNames.get(fileName), extension);
        }
    }
}
