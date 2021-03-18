package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.File;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.processor.Helpers;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.cli.CommandLineInterface;

public class Main {

    public static void main(String[] args) throws Exception {

        /*
         * File jarFile = new File(
         * "/home/anonym/projects/crce-ws-parser-dp/crce-client-webservices-indexer/src/main/java/cz/zcu/kiv/crce/examples/asm/test_12.jar"
         * );
         */
        File jarFile = CommandLineInterface.getFile(args);
        if (jarFile == null) {
            return;
        }
        Loader.loadClasses(jarFile);

        Processor.processMany(Helpers.convertStructMap(Collector.getInstance().getClasses()));
    }
}
