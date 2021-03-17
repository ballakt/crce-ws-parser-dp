package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;

public class Processor {

    public static Map<String, Endpoint> processMany(ClassMap classes) {
        Map<String, Endpoint> endpoints = new HashMap<>();
        EndpointProcessor classProcessor = new EndpointProcessor(classes);
        for (ClassWrapper class_ : classes.values()) {
            classProcessor.process(class_);
            Helpers.EndpointF.merge(endpoints, classProcessor.getEndpoints());
        }
        return endpoints;
    }
}
