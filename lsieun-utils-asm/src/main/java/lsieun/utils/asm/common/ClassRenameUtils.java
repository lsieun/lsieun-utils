package lsieun.utils.asm.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;

public class ClassRenameUtils {
    public static byte[] rename(byte[] bytes, String oldInternalName, String newInternalName) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连 ClassVisitor
        Remapper remapper = new SimpleRemapper(oldInternalName, newInternalName);
        ClassVisitor cv = new ClassRemapper(cw, remapper);

        //（4）两者进行结合
        int parsingOptions = 0;
        cr.accept(cv, parsingOptions);

        //（5）重新生成 Class
        return cw.toByteArray();
    }
}
