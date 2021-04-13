package cz.zcu.kiv.crce.classmodel.definition;

import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EDataContainerMethodConfig {

    @JsonProperty
    private String name;

    @JsonProperty("args")
    private Set<ArrayList<ArgConfigType>> args;

    /**
     * @return the args
     */
    public Set<ArrayList<ArgConfigType>> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArrayList<ArgConfigType>> args) {
        this.args = args;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
