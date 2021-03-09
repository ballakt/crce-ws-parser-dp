package cz.zcu.kiv.crce.classmodel.definition;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArgsMethodDefinition {
    @JsonProperty
    private Set<ArgsTypeMethodDefinition> args;

    /**
     * @return the args
     */
    public Set<ArgsTypeMethodDefinition> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArgsTypeMethodDefinition> args) {
        this.args = args;
    }

    enum ArgType {
        URL, URL_PARAM, RETRIEVE;
    }
}
