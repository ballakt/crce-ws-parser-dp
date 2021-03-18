package cz.zcu.kiv.crce.classmodel.processor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.extracting.Loader;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;

public class Processor {

    static Logger logger = LogManager.getLogger("endpoints");

    public static Map<String, Endpoint> process(File jar) throws IOException {
        Loader.loadClasses(jar);
        Map<String, Endpoint> endpoints = new HashMap<>();
        ClassMap classes = Helpers.convertStructMap(Collector.getInstance().getClasses());
        EndpointProcessor classProcessor = new EndpointProcessor(classes);

        for (ClassWrapper class_ : classes.values()) {
            classProcessor.process(class_);
            Helpers.EndpointF.merge(endpoints, classProcessor.getEndpoints());
        }
        for (final Endpoint endpoint : endpoints.values()) {
            logger.info(endpoint);
        }
        return endpoints;
    }
}
