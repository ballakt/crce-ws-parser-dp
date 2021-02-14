package cz.zcu.kiv.crce.classmodel.extracting;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.structures.Field;

public class MyFieldVisitor extends FieldVisitor {

    private State state = State.getInstance();
    private Field field;

    public MyFieldVisitor(Field field) {
        super(Opcodes.ASM9);
        this.field = field;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        // TODO Auto-generated method stub
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        // TODO Auto-generated method stub
        state.getClassType().addField(field);
        super.visitEnd();
    }

}
