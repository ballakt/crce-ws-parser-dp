package cz.zcu.kiv.crce.classmodel.extracting;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.DataType;
import cz.zcu.kiv.crce.classmodel.structures.Field;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class EClassVisitor extends ClassVisitor {

    private State state = State.getInstance();

    public EClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        System.out.println("Class-Visitor[" + name + "]=============START");
        System.out.println("name=" + name);
        System.out.println("extends=" + superName);
        ClassStruct class_ = new ClassStruct(name, superName);
        state.setClassType(class_);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        // TODO Auto-generated method stub
        System.out.println("Class-Visitor[" + State.getInstance().getClassType().getName()
                + "]==============END");
        Collector.getInstance().addClass(State.getInstance().getClassType());
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
            Object value) {
        System.out.println("Field-Visitor============START");
        System.out.println("ACCSESS: " + access);
        System.out.println("NAME: " + name);
        System.out.println("DESC: " + desc);
        System.out.println("SIGNATURE: " + signature);
        if (value != null) {
            System.out.println("VALUE: " + value);
        }
        //
        // Field field = new Field(dataType);
        // field.setName(name);
        // field.setAccess(access);
        // return new MyFieldVisitor(field);
        FieldVisitor data = super.visitField(access, name, desc, signature, value);
        System.out.println("Field-Visitor============END");

        DataType dataType = BytecodeDescriptorsProcessor.processFieldDescriptor(desc);
        Field field = new Field(dataType);
        field.setName(name);
        field.setAccess(access);
        return new MyFieldVisitor(field);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        // TODO Auto-generated method stub
        System.out.println("Method-Visitor[" + name + " " + name == "<clinit>" ? "TRUE"
                : "FALSE" + "]============START");
        Method newMethod = new Method(access, name, descriptor);

        if (name.equals("<clinit>")) {
            state.getClassType().setClnitMethod(newMethod);
        } else {
            state.getClassType().addMethod(newMethod);
        }
        MethodVisitor mv = new MyMethodVisitor(newMethod);

        return mv;
        // return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}
