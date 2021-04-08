package cz.zcu.kiv.crce.classmodel.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;

public class EnumFieldOrMethod {
    @JsonProperty("name")
    private String name;

    @JsonProperty
    private HttpMethod httpMethod;

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
     * @return the httpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod the httpMethod to set
     */
    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
