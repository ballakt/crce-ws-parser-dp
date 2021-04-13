package cz.zcu.kiv.crce.classmodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;

public class Collector {
    private ClassMap resources;
    static Logger logger = LogManager.getLogger("endpoints");

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
