package cz.zcu.kiv.crce.classmodel.definition;

import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MethodDefinition {
    @JsonProperty("name")
    private String name;

    @JsonProperty("args")
    private Set<ArrayList<ArgDefinitionType>> args;

    @JsonProperty("type")
    private DefinitionType type;

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
    public Set<ArrayList<ArgDefinitionType>> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArrayList<ArgDefinitionType>> args) {
        this.args = args;
    }

    /**
     * @return the type
     */
    public DefinitionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DefinitionType type) {
        this.type = type;
    }

    /*
     * private String argsToString() { String argsString = "";
     * 
     * this.args. }
     */

    @Override
    public String toString() {
        return "MethodDefinition{" + "name='" + name + '\'' + ", types='" + this.type.toString()
                + '\'' + ", args=" + this.args.toString() + '}';
    }
}
