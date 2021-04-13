package cz.zcu.kiv.crce.classmodel.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EndpointDataContainer {
    @JsonProperty("class")
    private String className;

    @JsonProperty
    private String constructor;

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
     * @return the constructor
     */
    public String getConstructor() {
        return constructor;
    }

    /**
     * @param constructor the constructor to set
     */
    public void setConstructor(String constructor) {
        this.constructor = constructor;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "EndpointDataContainer";
    }
}
