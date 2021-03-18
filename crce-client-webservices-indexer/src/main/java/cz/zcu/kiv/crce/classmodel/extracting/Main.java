package cz.zcu.kiv.crce.classmodel.extracting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.processor.Helpers;
import cz.zcu.kiv.crce.classmodel.processor.Processor;
import cz.zcu.kiv.crce.classmodel.structures.Endpoint;

public class Main {
    /*
     * EClassVisitor visitor;
     */
    /*
     * void parseClass(ClassReader classReader, EClassVisitor visitor) { classReader.accept(visitor,
     * ClassReader.SKIP_DEBUG);
     * 
     * }
     */

    static void loadClasses(File jarFile) throws IOException {
        // Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
        JarFile jar = new JarFile(jarFile);
        Stream<JarEntry> str = jar.stream();
        str.forEach(z -> readJar(jar, z));
        jar.close();
        // return classes;
    }

    static void readJar(JarFile jar, JarEntry entry) {
        String name = entry.getName();
        try (InputStream jis = jar.getInputStream(entry)) {
            if (name.endsWith(".class")) {
                EClassVisitor classVisitor = new EClassVisitor(Opcodes.ASM9, null);
                ClassReader classReader = new ClassReader(getEntryInputStream(jis));
                classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static InputStream getEntryInputStream(InputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = jis.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, n);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static void main(String[] args) throws Exception {
        File jarFile = new File(
                "/home/anonym/projects/crce-ws-parser-dp/crce-client-webservices-indexer/src/main/java/cz/zcu/kiv/crce/examples/asm/test_12.jar");
        loadClasses(jarFile);

        // Collector.getInstance().process();
        Map<String, Endpoint> endpoints = Processor
                .processMany(Helpers.convertStructMap(Collector.getInstance().getClasses()));


        System.out.println("============FINAL ENDPOINTS==================");

        for (Endpoint endpoint : endpoints.values()) {
            System.out.println(endpoint);
        }



    }
}
