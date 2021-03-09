package cz.zcu.kiv.crce.classmodel.definition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Definition {
    private static final String DEF_PATH = "/definition";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static MethodDefinitionMap loadDefinitions() throws Exception {
        MethodDefinitionMap methoDefinitions = new MethodDefinitionMap();
        // Map<String, DefinitionItem> restApiDefinitions = new HashMap<>();
        final URL resource_url = Definition.class.getResource(DEF_PATH);
        if (resource_url == null) {
            throw new Exception("directory not found: " + DEF_PATH);
        }

        File directory = new File(resource_url.toURI());
        File[] filesInDirectory = directory.listFiles();

        for (int i = 0; i < filesInDirectory.length; i++) {
            final String path = DEF_PATH + "/" + filesInDirectory[i].getName();
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
