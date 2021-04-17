package cz.zcu.kiv.crce.rest;

import java.io.Serializable;
import java.util.Objects;
import cz.zcu.kiv.crce.classmodel.processor.tools.ToStringTools;

/**
 * Created by ghessova on 10.03.2018.
 */
public class EndpointParameter implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6837202021942397888L;

    private String name = "";
    private String dataType = ""; // T
    private boolean isArray = false;

    private ParameterCategory category;

    public EndpointParameter(String name, String datatype, boolean isArray,
            ParameterCategory category) {
        this(name, datatype, isArray);
        this.category = category;
    }

    public EndpointParameter(String name, String datatype, boolean isArray) {
        this.name = name != null ? name : "";
        this.dataType = datatype;
        this.isArray = isArray;
    }

    public EndpointParameter() {

    }

    public ParameterCategory getCategory() {
        return category;
    }

    public void setCategory(ParameterCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    /*
     * @Override public boolean equals(Object obj) { // TODO Auto-generated method stub return
     * super.equals(obj); }
     */

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType, isArray, category);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndpointParameter) {
            EndpointParameter eParam = (EndpointParameter) obj;
            final boolean nameEq = name == eParam.getName() || name.equals(eParam.getName());
            final boolean dataTypeEq =
                    dataType == eParam.getDataType() || dataType.equals(eParam.getDataType());
            final boolean isArrayEq = isArray == eParam.isArray();
            final boolean categoryEq = category == eParam.getCategory();
            return nameEq && dataTypeEq && isArrayEq && categoryEq;
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + "\"name\": " + ToStringTools.objToString(getName()) + ", \"category\": "
                + ToStringTools.objToString(getCategory()) + ", \"dataType\": "
                + ToStringTools.objToString(getDataType()) + ", \"isArray\": " + isArray() + "}";
    }
}
