package cz.zcu.kiv.crce.classmodel.definition;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestApiDefinition {
    @JsonProperty("name")
    private String name;

    @JsonProperty
    private Set<DefinitionItem> definitions;

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
     * @return the definitions
     */
    public Set<DefinitionItem> getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions the definitions to set
     */
    public void setDefinitions(Set<DefinitionItem> definitions) {
        this.definitions = definitions;
    }
}
