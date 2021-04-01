package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.File;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.cli.CommandLineInterface;

public class Main {

    public static void main(String[] args) throws Exception {



        /*
         * File jarFile = new File(
         * "/home/anonym/projects/crce-ws-parser-dp/crce-client-webservices-indexer/src/test/resources/spring_webclient.jar"
         * );
         */
        File jarFile = CommandLineInterface.getFile(args);

        if (jarFile == null) {
            return;
        }

        Processor.process(jarFile);
    }
}
