package cz.zcu.kiv.crce.classmodel.processor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.extracting.Loader;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;

public class Processor {
    /**
     * Finds endpoints in JAR file
     * 
     * @param jar JAR file
     * @return
     * @throws IOException
     */
    public static Map<String, Endpoint> process(File jar) throws IOException {
        Collector.init();
        Loader.loadClasses(jar);
        Map<String, Endpoint> endpoints = new HashMap<>();
        ClassMap classes = Helpers.convertStructMap(Collector.getInstance().getClasses());
        EndpointProcessor endpointProcessor = new EndpointProcessor(classes);

        for (ClassWrapper class_ : classes.values()) {
            endpointProcessor.process(class_);
            Helpers.EndpointF.merge(endpoints, endpointProcessor.getEndpoints());
        }
        return endpoints;
    }
}
