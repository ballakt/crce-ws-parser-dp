package cz.zcu.kiv.crce.classmodel.definition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Definition {

    private static MethodDefinitionMap definition = null;
    private static final String DEF_DIR_NAME = "definition";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static void loadFilesFromJar(List<String> filesInDirectory, String fullPath)
            throws IOException {
        String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        filesInDirectory = new LinkedList<String>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith(DEF_DIR_REL) && !entryName.equals(DEF_DIR_REL)) {
                filesInDirectory.add(entryName);
            }
        }
        jarFile.close();
    }

    private static void loadConfigurationFile(String defDirPath, String file) throws Exception {
        final String path = defDirPath + "/" + file;
        final InputStream inputStream = Definition.class.getResourceAsStream(path);
        if (inputStream == null)
            throw new Exception("resource not found: " + path);
        RestApiDefinition restApiDefinition =
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(inputStream, RestApiDefinition.class);
        Set<DefinitionItem> definitions = restApiDefinition.getDefinitions();

        for (DefinitionItem one : definitions) {
            final String methodDefKey = one.getClassName();
            if (!definition.containsKey(methodDefKey)) {
                definition.put(methodDefKey, new HashMap<>());
            }
            for (MethodDefinition md : one.getMethods()) {
                definition.get(methodDefKey).put(md.getName(), md);
            }
        }
    }

    private static void loadDefinitions() throws Exception {
        String defDirPath = DEF_DIR_ABS;

        definition = new MethodDefinitionMap();
        // Map<String, DefinitionItem> restApiDefinitions = new HashMap<>();
        final URL resource_url = Definition.class.getResource(defDirPath);
        if (resource_url == null) {
            throw new Exception("directory not found: " + defDirPath);
        }

        File directory = null;
        String fullPath = resource_url.getFile();
        List<String> filesInDirectory = null;

        try {
            directory = new File(resource_url.toURI());
        } catch (IllegalArgumentException e) {
            defDirPath = "";
            loadFilesFromJar(filesInDirectory, fullPath);
        }

        if (directory != null) {
            filesInDirectory = Arrays.asList(directory.list());
        }
        for (final String file : filesInDirectory) {
            loadConfigurationFile(defDirPath, file);
        }
    }

    public static MethodDefinitionMap getDefinitions() {
        if (definition == null) {
            try {
                loadDefinitions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return definition;
    }
}
