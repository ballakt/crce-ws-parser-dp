package cz.zcu.kiv.crce.parser;

import java.io.File;

public interface IParser {
    /**
     * Parsing file into some structure
     * 
     * @param file
     */
    public void parse(File file);
}
