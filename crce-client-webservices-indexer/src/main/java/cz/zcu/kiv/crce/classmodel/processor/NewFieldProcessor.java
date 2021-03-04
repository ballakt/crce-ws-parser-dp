package cz.zcu.kiv.crce.classmodel.processor;

import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;

public class NewFieldProcessor extends NewMethodProcessor {

    public static final String INIT_STATIC = "<clinit>";
    public static final String INIT = "<init>";

    public NewFieldProcessor(ClassMap classes) {
        super(classes);
    }

    public void process(ClassWrapper class_) {

        ConstPool classPool = class_.getClassPool();
        MethodWrapper cInit = class_.getMethod(INIT_STATIC);
        MethodWrapper init = class_.getMethod(INIT);
        if (cInit != null) {
            super.process(cInit);
            class_.removeMethod(INIT_STATIC);
        }

        if (init != null) {
            super.process(init);
            class_.removeMethod(INIT);
        }
        // static part
        // dynamic part
    }
}
