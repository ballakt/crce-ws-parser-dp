package cz.zcu.kiv.crce.classmodel.structures;

/**
 * Created by ghessova on 01.05.2018.
 */
public class Field extends Variable {

    private int access;
    private String val;

    public Field(DataType dataType) {
        super(dataType);
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setConstValue(String val) {
        this.val = val;
    }

    public String getConstValue() {
        return val;
    }

    @Override
    public String toString() {
        return super.toString() + ", Field{" + "access=" + access + '}';
    }
}
