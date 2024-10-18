package lsieun.utils.asm.common.generation;

import lsieun.utils.asm.common.analysis.ClassFileFindUtils;
import lsieun.utils.asm.core.AsmTypeBuddy;
import lsieun.utils.asm.description.ByteCodeElementType;
import lsieun.utils.asm.insn.AsmInsnUtilsForOpcode;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.transformation.add.ClassVisitorForAddInnerClass;
import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ClassFileGenerationUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassFileGenerationUtils.class);

    public static void generateMatch(Class<?> clazz) throws IOException {
        Path path = ResourceUtils.readFilePath(clazz);
        generateMatch(path);
    }

    public static void generateMatch(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);

        ClassReader cr = new ClassReader(bytes);
        String className = cr.getClassName();
        logger.info(MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.TYPE, className));
        int access = cr.getAccess();
        boolean isInterface = (access & ACC_INTERFACE) != 0;
        if (!isInterface) {
            logger.info(MatchFormat.format(MatchState.SKIP, ByteCodeElementType.TYPE, className + " is not an interface"));
            return;
        }

        MethodInfoMatch methodMatch = MethodInfoMatch.byModifier(Modifier::isAbstract);
        List<MatchItem> methodList = ClassFileFindUtils.findMethod(bytes, methodMatch);
        if (methodList.isEmpty()) {
            logger.info(MatchFormat.format(MatchState.SKIP, ByteCodeElementType.TYPE,
                    className + " does not have an abstract method"));
            return;
        }
        if (methodList.size() > 1) {
            logger.info(MatchFormat.format(MatchState.SKIP, ByteCodeElementType.TYPE,
                    className + " has more than one abstract method"));
            return;
        }
        logger.info(MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.TYPE, className));

        MatchItem matchItem = methodList.get(0);
        String owner = matchItem.owner;
        String samMethodName = matchItem.name;
        String samMethodDesc = matchItem.descriptor;
        Type samMethodType = Type.getMethodType(samMethodDesc);

        logger.info(String.format("[GENERATE] %s", className));
        String[] innerNames = {"All", "None"};
        byte[] newBytes = addInnerClass(bytes, innerNames);
        FileContentUtils.writeBytes(path, newBytes);

        logger.info(String.format("[GENERATE] %s", getInnerClassName(className, "All")));
        generateNew(path, "All", owner, samMethodName, samMethodDesc, true);
        logger.info(String.format("[GENERATE] %s", getInnerClassName(className, "None")));
        generateNew(path, "None", owner, samMethodName, samMethodDesc, false);
    }

    public static byte[] addInnerClass(byte[] bytes, String... innerNames) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitorForAddInnerClass(cw, innerNames);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    public static void generateNew(Path path, String innerName, String owner, String methodName, String methodDesc, boolean flag) throws IOException {
        Path parent = path.getParent();
        String filename = path.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf("."));
        String newFilename = String.format("%s$%s.class", filename, innerName);
        Path newFilePath = parent.resolve(newFilename);

        byte[] bytes = ClassFileGenerationUtils.generateLogic(
                Type.getObjectType(owner),
                innerName,
                methodName,
                Type.getMethodType(methodDesc),
                flag
        );
        FileContentUtils.writeBytes(newFilePath, bytes);
    }

    public static byte[] generateLogic(Type outerType, String innerName,
                                       String samMethodName, Type samMethodType,
                                       boolean flag) {
        // outer
        String outerClassInternalName = outerType.getInternalName();
        String outerClassDescriptor = outerType.getDescriptor();

        // inner
        Type innerType = Type.getObjectType(getInnerClassName(outerClassInternalName, innerName));
        String innerClassInternalName = innerType.getInternalName();
        String innerClassDescriptor = innerType.getDescriptor();
        String innerClassSignature = String.format("Ljava/lang/Enum<%s>;%s", innerClassDescriptor, outerClassDescriptor);

        // inner array
        Type innerClassArrayType = AsmTypeBuddy.toArray(innerType, 1);
        String innerClassArrayDescriptor = innerClassArrayType.getDescriptor();

        // void -> inner array
        Type void2InnerArrayMethodType = Type.getMethodType(innerClassArrayType);
        // str --> inner
        Type str2InnerMethodType = Type.getMethodType(innerType, Type.getType(String.class));


        // ASM
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        cw.visit(V17, ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_ENUM,
                innerClassInternalName, innerClassSignature,
                "java/lang/Enum", new String[]{outerClassInternalName});

        cw.visitNestHost(outerClassInternalName);
        cw.visitInnerClass(innerClassInternalName, outerClassInternalName, "All", ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

        {
            FieldVisitor fv = cw.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM,
                    "INSTANCE", innerClassDescriptor, null, null);
            fv.visitEnd();
        }
        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_SYNTHETIC,
                    "$VALUES", innerClassArrayDescriptor, null, null);
            fv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
                    "values", void2InnerArrayMethodType.getDescriptor(), null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, innerClassInternalName, "$VALUES", innerClassArrayDescriptor);
            mv.visitMethodInsn(INVOKEVIRTUAL, innerClassArrayDescriptor, "clone", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, innerClassArrayDescriptor);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
                    "valueOf", str2InnerMethodType.getDescriptor(), null, null);
            mv.visitCode();
            mv.visitLdcInsn(Type.getType(innerClassDescriptor));
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
            mv.visitTypeInsn(CHECKCAST, innerClassInternalName);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE,
                    "<init>", "(Ljava/lang/String;I)V", "()V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            int maxLocals = AsmInsnUtilsForOpcode.getFirstLocalSlotIndex(false, samMethodType.getDescriptor());
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, samMethodName, samMethodType.getDescriptor(), null, null);
            mv.visitCode();
            mv.visitInsn(flag ? ICONST_1 : ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, maxLocals);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                    "$values", void2InnerArrayMethodType.getDescriptor(), null, null);
            mv.visitCode();
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, innerClassInternalName);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitFieldInsn(GETSTATIC, innerClassInternalName, "INSTANCE", innerClassDescriptor);
            mv.visitInsn(AASTORE);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 0);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, innerClassInternalName);
            mv.visitInsn(DUP);
            mv.visitLdcInsn("INSTANCE");
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKESPECIAL, innerClassInternalName, "<init>", "(Ljava/lang/String;I)V", false);
            mv.visitFieldInsn(PUTSTATIC, innerClassInternalName, "INSTANCE", innerClassDescriptor);
            mv.visitMethodInsn(INVOKESTATIC, innerClassInternalName, "$values", void2InnerArrayMethodType.getDescriptor(), false);
            mv.visitFieldInsn(PUTSTATIC, innerClassInternalName, "$VALUES", innerClassArrayDescriptor);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static String getInnerClassName(String outerClassInternalName, String innerName) {
        return String.format("%s$%s", outerClassInternalName, innerName);
    }
}
