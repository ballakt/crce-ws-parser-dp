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
        for (final Endpoint endpoint : endpoints.values()) {
            logger.info(endpoint);
            // TODO: predelat do yml
            // TODO: vyjmout tisk do mainu
        }

        return endpoints;
    }
}
