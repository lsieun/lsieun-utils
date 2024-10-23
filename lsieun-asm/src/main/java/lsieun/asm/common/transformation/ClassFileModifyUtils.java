package lsieun.asm.common.transformation;

import lsieun.asm.sam.consumer.InsnInvokeConsumer;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.transformation.clazz.TypeAddToStringMethodVisitor;
import lsieun.asm.visitor.transformation.insn.InsnTraceVisitor;
import lsieun.asm.visitor.transformation.insn.ModifyInsnInvokeVisitor;
import lsieun.asm.visitor.transformation.method.ClassVisitorForMethodBodyEmpty;
import lsieun.asm.visitor.transformation.method.ClassVisitorForMethodBodyInfo;
import lsieun.asm.visitor.transformation.method.MethodBodyInfoType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.Set;

@SuppressWarnings("UnnecessaryLocalVariable")
public class ClassFileModifyUtils {

    public static byte @Nullable [] printMethodInfo(byte @NotNull [] bytes,
                                                    @NotNull MethodInfoMatch match,
                                                    @NotNull Set<MethodBodyInfoType> options) {
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
        ClassVisitor cv = new ModifyInsnInvokeVisitor(cw, methodMatch, insnInvokeMatch, insnInvokeConsumer, supportJump);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }

    /**
     * @see TypeAddToStringMethodVisitor
     */
    public static byte[] addToString(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new TypeAddToStringMethodVisitor(cw);
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);
        return cw.toByteArray();
    }

    public static byte[] traceInsn(byte[] bytes, MethodInfoMatch methodMatch) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        int writerFlag = ClassWriter.COMPUTE_FRAMES;
        ClassWriter cw = new ClassWriter(writerFlag);

        //（3）串连ClassVisitor
        ClassVisitor cv = new InsnTraceVisitor(cw, methodMatch);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] newBytes = cw.toByteArray();
        return newBytes;
    }
}
