package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;

public class Processor {

    /**
     * Retrieves endpoints from each class
     * 
     * @param classes Map of classes
     * @return Endpoints maped by their URIs
     */
    static Logger logger = LogManager.getLogger("endpoints");

    public static Map<String, Endpoint> processMany(ClassMap classes) {
        Map<String, Endpoint> endpoints = new HashMap<>();
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
