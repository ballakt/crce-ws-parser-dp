package cz.zcu.kiv.crce.examples.asm;

import org.objectweb.asm.ClassVisitor;

public class EClassVisitor extends ClassVisitor {

    public EClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

}
