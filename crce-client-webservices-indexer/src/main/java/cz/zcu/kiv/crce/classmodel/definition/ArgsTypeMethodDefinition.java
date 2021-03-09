package cz.zcu.kiv.crce.classmodel.definition;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArgsTypeMethodDefinition {
    @JsonProperty
    private ArrayList<String> arg;

    /**
     * @return the arg
     */
    public ArrayList<String> getArg() {
        return arg;
    }

    /**
     * @param arg the arg to set
     */
    public void setArg(ArrayList<String> arg) {
        this.arg = arg;
    }
}
