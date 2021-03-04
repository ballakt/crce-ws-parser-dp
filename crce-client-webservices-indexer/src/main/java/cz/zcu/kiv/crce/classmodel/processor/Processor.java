package cz.zcu.kiv.crce.classmodel.processor;

import java.util.LinkedList;
import java.util.List;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;

public class Processor {

    public static List<Endpoint> processMany(ClassMap classes) {
        List<Endpoint> endpoints = new LinkedList<>();
        EndpointProcessor classProcessor = new EndpointProcessor(classes);
        for (ClassWrapper class_ : classes.values()) {
            classProcessor.process(class_);
            endpoints.addAll(classProcessor.getEndpoints());
        }
        return endpoints;
    }
}
