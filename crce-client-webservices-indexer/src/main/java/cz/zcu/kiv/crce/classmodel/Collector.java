package cz.zcu.kiv.crce.classmodel;

import cz.zcu.kiv.crce.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;

public class Collector {
    private ClassMap resources;

    private static Collector ourInstance;

    public static void init() {
        ourInstance = new Collector();
    }

    public static Collector getInstance() {
        return ourInstance;
    }

    private Collector() {
        this.resources = new ClassMap();
    }

    public ClassMap getClasses() {
        return resources;
    }

    public void addClass(ClassStruct resource) {
        resources.put(resource.getName(), resource);
    }

}
