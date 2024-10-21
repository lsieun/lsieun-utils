package lsieun.asm.common.transformation;

import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.visitor.transformation.modify.clazz.ClassVisitorForToString;
import lsieun.asm.visitor.transformation.modify.insn.ClassVisitorForModifyInsnInvoke;
import lsieun.asm.visitor.transformation.modify.method.ClassVisitorForMethodBodyEmpty;
import lsieun.asm.visitor.transformation.modify.method.ClassVisitorForMethodBodyInfo;
import lsieun.asm.visitor.transformation.modify.method.MethodBodyInfoType;
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

    public static byte[] emptyMethodBody(byte[] bytes, MethodInfoMatch methodMatch, Object returnValue) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassVisitorForMethodBodyEmpty(cw, methodMatch, returnValue);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }


    public static byte[] emptyAndPrint(byte[] bytes, MethodInfoMatch match, Object returnValue, Set<MethodBodyInfoType> options) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        //（3）串连ClassVisitor
        ClassVisitor printVisitor = new ClassVisitorForMethodBodyInfo(cw, match, options);
        ClassVisitor emptyVisitor = new ClassVisitorForMethodBodyEmpty(printVisitor, match, returnValue);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(emptyVisitor, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }

    public static byte[] modifyInsnInvoke(byte[] bytes,
                                          MethodInfoMatch methodMatch,
                                          InsnInvokeMatch insnInvokeMatch,
                                          InsnInvokeConsumer insnInvokeConsumer) {
        return modifyInsnInvoke(bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer, false);
    }

    public static byte[] modifyInsnInvoke(byte[] bytes,
                                          MethodInfoMatch methodMatch,
                                          InsnInvokeMatch insnInvokeMatch,
                                          InsnInvokeConsumer insnInvokeConsumer,
                                          boolean supportJump) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        int writerFlag = supportJump ? ClassWriter.COMPUTE_FRAMES : ClassWriter.COMPUTE_MAXS;
        ClassWriter cw = new ClassWriter(writerFlag);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassVisitorForModifyInsnInvoke(cw, methodMatch, insnInvokeMatch, insnInvokeConsumer, supportJump);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }

    /**
     * @see ClassVisitorForToString
     */
    public static byte[] addToString(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitorForToString(cw);
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);
        return cw.toByteArray();
    }
}
