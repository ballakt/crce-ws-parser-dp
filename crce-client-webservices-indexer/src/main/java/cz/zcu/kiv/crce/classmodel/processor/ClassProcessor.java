package cz.zcu.kiv.crce.classmodel.processor;

import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;

public class ClassProcessor {

    ClassMap classes;
    FieldProcessor fieldProcessor;
    MethodProcessor methodProcessor;


    public ClassProcessor(ClassMap classes) {
        this.classes = classes;

        fieldProcessor = new FieldProcessor(classes);
        methodProcessor = new MethodProcessor(classes);
    }

    /**
     * Process class its methods and fields
     * 
     * @param class_ Class to processing
     */
    public void process(ClassWrapper class_) {
        // first process fields
        this.fieldProcessor.process(class_);
        // next porocess methods
        for (MethodWrapper method : class_.getMethods()) {
            this.methodProcessor.process(method);
        }
    }

}
