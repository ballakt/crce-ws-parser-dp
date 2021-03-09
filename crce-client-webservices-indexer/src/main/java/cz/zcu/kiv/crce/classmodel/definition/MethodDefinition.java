package cz.zcu.kiv.crce.classmodel.definition;

import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MethodDefinition {
    @JsonProperty("name")
    private String name;

    @JsonProperty("args")
    private Set<ArrayList<String>> args;

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

    /**
     * @return the args
     */
    public Set<ArrayList<String>> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArrayList<String>> args) {
        this.args = args;
    }


}
