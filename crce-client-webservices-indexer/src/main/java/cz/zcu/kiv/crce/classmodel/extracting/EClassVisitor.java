package cz.zcu.kiv.crce.classmodel.extracting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.classmodel.structures.DataType;
import cz.zcu.kiv.crce.classmodel.structures.Field;
import cz.zcu.kiv.crce.classmodel.structures.Method;

public class EClassVisitor extends ClassVisitor {

    static Logger log = LogManager.getLogger("extractor");

    private State state = State.getInstance();

    public EClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        log.debug("============Class-Visitor[name=" + name + " extends=" + superName + "]===");
        ClassStruct class_ = new ClassStruct(name, superName);
        state.setClassType(class_);
        super.visit(version, access, name, signature, superName, interfaces);

    }

    @Override
    public void visitEnd() {
        log.debug("==========" + "END-Class-Visitor[" + State.getInstance().getClassType().getName()
                + "]" + "==================\n\n\n");
        Collector.getInstance().addClass(State.getInstance().getClassType());
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
            Object value) {
        log.debug("    Field-Visitor[access=" + access + ", name=" + name + ", desc=" + desc
                + ", signature=" + signature + ", value=" + value + "]");
        //
        // Field field = new Field(dataType);
        // field.setName(name);
        // field.setAccess(access);
        // return new MyFieldVisitor(field);
        // FieldVisitor data = super.visitField(access, name, desc, signature, value);

        DataType dataType = BytecodeDescriptorsProcessor.processFieldDescriptor(desc);
        Field field = new Field(dataType);
        field.setName(name);
        field.setAccess(access);
        return new MyFieldVisitor(field);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        log.debug("\n    ==========Method-Visitor[name=" + name + " CLINIT="
                + (name.equals("<clinit>") ? "TRUE" : "FALSE") + "]===");
        Method newMethod = new Method(access, name, descriptor);
        state.getClassType().addMethod(newMethod);
        MethodVisitor mv = new MyMethodVisitor(newMethod);

        return mv;
        // return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}
