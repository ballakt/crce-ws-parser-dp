package cz.zcu.kiv.crce.classmodel.processor;

public class EndpointRequestBody {
    private String structure;
    private boolean isArray;


    public EndpointRequestBody(String structure, boolean isArray) {
        this.structure = structure;
        this.isArray = isArray;
    }

    public EndpointRequestBody() {
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }


    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    @Override
    public String toString() {
        return "RequestBody{structure=" + structure + ", isArray='" + isArray + '\'' + "'}'";
    }

    @Override
    public int hashCode() {
        return (getStructure() + isArray).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndpointRequestBody) {
            EndpointRequestBody eReqBody = (EndpointRequestBody) obj;
            boolean structureEq = structure.equals(eReqBody.getStructure());
            boolean isArrayEq = isArray == eReqBody.isArray();
            return structureEq && isArrayEq;
        }
        return false;
    }
}
