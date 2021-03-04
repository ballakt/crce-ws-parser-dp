package cz.zcu.kiv.crce.classmodel.processor;

import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;

public class ClassProcessor {

    ClassMap classes;
    NewFieldProcessor fieldProcessor;
    NewMethodProcessor methodProcessor;


    public ClassProcessor(ClassMap classes) {
        this.classes = classes;

        fieldProcessor = new NewFieldProcessor(classes);
        methodProcessor = new NewMethodProcessor(classes);
    }

    public void process(ClassWrapper class_) {
        // first process fields
        this.fieldProcessor.process(class_);
        // next porocess methods
        for (MethodWrapper method : class_.getMethods()) {
            this.methodProcessor.process(method);
        }
    }

}
