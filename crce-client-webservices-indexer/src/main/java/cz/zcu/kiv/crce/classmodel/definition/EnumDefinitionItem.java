package cz.zcu.kiv.crce.classmodel.definition;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumDefinitionItem {
    @JsonProperty("class")
    private String className;

    @JsonProperty
    private Set<EnumFieldOrMethod> methods;

    @JsonProperty
    private Set<EnumFieldOrMethod> fields;

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
        String processed = DefinitionValuesProcessor.processClassName(className);
        this.className = processed;
    }

    /**
     * @return the methods
     */
    public Set<EnumFieldOrMethod> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<EnumFieldOrMethod> methods) {
        this.methods = methods;
    }

    /**
     * @return the fields
     */
    public Set<EnumFieldOrMethod> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Set<EnumFieldOrMethod> fields) {
        this.fields = fields;
    }


}
