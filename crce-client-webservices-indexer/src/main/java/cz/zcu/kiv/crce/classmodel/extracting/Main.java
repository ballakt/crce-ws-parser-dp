package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.File;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.cli.CommandLineInterface;


public class Main {



    public static void main(String[] args) throws Exception {

        File jarFile = CommandLineInterface.getFile(args);

        if (jarFile == null) {
            return;
        }


        Processor.process(jarFile);

    }
}
