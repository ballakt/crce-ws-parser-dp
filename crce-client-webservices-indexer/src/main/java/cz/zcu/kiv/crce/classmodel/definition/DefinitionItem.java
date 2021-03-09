package cz.zcu.kiv.crce.classmodel.definition;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefinitionItem {
    @JsonProperty("class")
    private String className;

    @JsonProperty("methods")
    private Set<MethodDefinition> methods;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the methods
     */
    public Set<MethodDefinition> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<MethodDefinition> methods) {
        this.methods = methods;
    }
}
