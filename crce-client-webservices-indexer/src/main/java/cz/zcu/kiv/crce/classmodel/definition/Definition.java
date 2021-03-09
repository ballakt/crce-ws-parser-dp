package cz.zcu.kiv.crce.classmodel.definition;

import java.io.File;
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
    private static final String DEF_DIR_NAME = "definition";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static MethodDefinitionMap loadDefinitions() throws Exception {

        String defDirPath = DEF_DIR_ABS;

        MethodDefinitionMap methoDefinitions = new MethodDefinitionMap();
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
        }
        if (directory != null) {
            filesInDirectory = Arrays.asList(directory.list());
        }
        for (final String file : filesInDirectory) {
            System.out.println("CONFIG_FILE=" + file);
            final String path = defDirPath + "/" + file;
            final InputStream inputStream = Definition.class.getResourceAsStream(path);
            if (inputStream == null)
                throw new Exception("resource not found: " + path);
            RestApiDefinition restApiDefinition =
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                            .readValue(inputStream, RestApiDefinition.class);
            Set<DefinitionItem> definitions = restApiDefinition.getDefinitions();

            for (DefinitionItem definition : definitions) {
                final String methodDefKey = definition.getClassName();
                if (!methoDefinitions.containsKey(methodDefKey)) {
                    methoDefinitions.put(methodDefKey, new HashMap<>());
                }
                for (MethodDefinition md : definition.getMethods()) {
                    methoDefinitions.get(methodDefKey).put(md.getName(), md);
                }
            }
        }
        return methoDefinitions;
    }
}
