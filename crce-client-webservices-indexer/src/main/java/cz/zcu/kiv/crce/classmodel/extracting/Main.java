package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.File;
import java.util.Collection;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.classmodel.processor.tools.ToStringTools;
import cz.zcu.kiv.crce.cli.CommandLineInterface;

public class Main {

    public static void main(String[] args) throws Exception {
        File jarFile = CommandLineInterface.getFile(args);

        if (jarFile == null) {
            return;
        }

        Collection<Endpoint> endpoints = Processor.process(jarFile).values();
        System.out.println(ToStringTools.endpointsToJSON(endpoints));
    }
}
