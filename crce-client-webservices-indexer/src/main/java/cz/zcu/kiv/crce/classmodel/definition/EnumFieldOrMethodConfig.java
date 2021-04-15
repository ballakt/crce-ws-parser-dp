package cz.zcu.kiv.crce.classmodel.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;

public class EnumFieldOrMethodConfig {
    @JsonProperty("name")
    private String name;

    @JsonProperty
    private HttpMethod httpMethod;

    @JsonProperty
    private String contentType;

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

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
