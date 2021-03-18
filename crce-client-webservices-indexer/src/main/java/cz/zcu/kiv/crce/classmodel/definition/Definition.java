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

    private static final String JAR_URI_SCHEME = "jar";
    private static MethodDefinitionMap definition = null;
    private static final String DEF_DIR_NAME = "definition";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Preloads all filenames into list (JAR version)
     * 
     * @param path Path to resource file
     * @throws IOException
     */
    private static List<String> loadFilesFromJar(String path) throws IOException {
        final String jarPath = path.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> filesInDirectory = new LinkedList<String>();

        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String entryName = entry.getName();

            if (entryName.startsWith(DEF_DIR_REL) && !entryName.equals(DEF_DIR_REL)) {
                filesInDirectory.add("/" + entryName);
            }
        }

        jarFile.close();
        return filesInDirectory;
    }

    /**
     * Wrappes loader for configuration file
     * 
     * @param defDirPath Directory of an configuration file
     * @param file       Filename of an configuration file
     * @throws Exception
     */
    private static void loadConfigurationFile(String defDirPath, String file) throws Exception {
        final String path = defDirPath + "/" + file;
        loadConfigurationFile(path);
    }

    /**
     * Loads configuration into private static field
     * 
     * @param path Path to configuration file
     * @throws Exception
     */
    private static void loadConfigurationFile(String path) throws Exception {
        final InputStream inputStream = Definition.class.getResourceAsStream(path);
        if (inputStream == null)
            throw new Exception("Resource not found: " + path);
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

    /**
     * Loads configuration file into definition map
     * 
     * @throws Exception
     */
    private static void loadDefinitions() throws Exception {
        final URL resource_url = Definition.class.getResource(DEF_DIR_ABS);

        if (resource_url == null) {
            throw new Exception("Directory not found: " + DEF_DIR_ABS);
        }

        File directory = null;
        List<String> filesInDirectory = null;
        definition = new MethodDefinitionMap();
        String fullPath = resource_url.getFile();

        if (resource_url.toURI().getScheme().equals(JAR_URI_SCHEME)) { // inside JAR
            filesInDirectory = loadFilesFromJar(fullPath);
            for (final String path : filesInDirectory) {
                loadConfigurationFile(path);
            }
        } else {
            directory = new File(resource_url.toURI());
            filesInDirectory = Arrays.asList(directory.list());
            for (final String file : filesInDirectory) {
                loadConfigurationFile(DEF_DIR_ABS, file);
            }
        }
    }

    /**
     * 
     * @return Definition of methods
     */
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
