package cz.zcu.kiv.crce.classmodel.definition;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestApiDefinition {
    @JsonProperty("name")
    private String name;

    @JsonProperty
    private Set<DefinitionItem> methods;

    @JsonProperty
    private Set<EnumDefinitionItem> enums;

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
     * @return the methods
     */
    public Set<DefinitionItem> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<DefinitionItem> methods) {
        this.methods = methods;
    }

    /**
     * @return the enums
     */
    public Set<EnumDefinitionItem> getEnums() {
        return enums;
    }

    /**
     * @param enums the enums to set
     */
    public void setEnums(Set<EnumDefinitionItem> enums) {
        this.enums = enums;
    }


}
