package lsieun.utils.asm.common;

import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.visitor.transformation.modify.method.ClassVisitorForMethodBodyEmpty;
import lsieun.utils.asm.visitor.transformation.modify.method.ClassVisitorForMethodBodyInfo;
import lsieun.utils.asm.visitor.transformation.modify.method.ClassVisitorForModifyInsnInvoke;
import lsieun.utils.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.Set;

@SuppressWarnings("UnnecessaryLocalVariable")
public class ClassFileModifyUtils {

    public static byte[] printMethodInfo(byte[] bytes, MethodInfoMatch match, Set<MethodBodyInfoType> options) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassVisitorForMethodBodyInfo(cw, match, options);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }

    public static byte[] emptyMethodBody(byte[] bytes, MethodInfoMatch match) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassVisitorForMethodBodyEmpty(cw, match);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }


    public static byte[] emptyAndPrint(byte[] bytes, MethodInfoMatch match, Set<MethodBodyInfoType> options) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor printVisitor = new ClassVisitorForMethodBodyInfo(cw, match, options);
        ClassVisitor emptyVisitor = new ClassVisitorForMethodBodyEmpty(printVisitor, match);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(emptyVisitor, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }


    public static byte[] patchInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch,
                                         InsnInvokeMatch insnInvokeMatch, InsnInvokeConsumer insnInvokeConsumer) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassVisitorForModifyInsnInvoke(cw, methodMatch, insnInvokeMatch, insnInvokeConsumer);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }
}
