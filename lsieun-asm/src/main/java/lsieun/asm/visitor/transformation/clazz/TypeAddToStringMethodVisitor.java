package lsieun.asm.visitor.transformation.clazz;

import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.FieldDesc;
import lsieun.asm.insn.desc.AsmInsnUtilsForDesc;
import lsieun.asm.sample.SampleForFieldToString;
import lsieun.asm.tag.AsmCodeTagCarrier;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @see ClassFileModifyUtils#addToString(byte[], boolean)
 */
public class TypeAddToStringMethodVisitor extends ClassVisitor implements AsmCodeTagCarrier.ForMax, Opcodes {
    private static final String TO_STRING_METHOD_NAME = "toString";
    private static final String TO_STRING_METHOD_DESC = "()Ljava/lang/String;";

    private final boolean supportStaticField;
    private boolean isInterface = false;
    private String currentOwner;
    private final List<FieldDesc> fieldList = new ArrayList<>();
    private boolean toStringExists = false;


    public TypeAddToStringMethodVisitor(ClassVisitor classVisitor, boolean supportStaticField) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.supportStaticField = supportStaticField;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        boolean isStaticField = (access & Opcodes.ACC_STATIC) != 0;
        if (!isStaticField) {
            FieldDesc fieldDesc = new FieldDesc(currentOwner, access, name, descriptor);
            fieldList.add(fieldDesc);
        }
        else if (supportStaticField) {
            FieldDesc fieldDesc = new FieldDesc(currentOwner, access, name, descriptor);
            fieldList.add(fieldDesc);
        }

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (TO_STRING_METHOD_NAME.equals(name) && TO_STRING_METHOD_DESC.equals(descriptor)) {
            toStringExists = true;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    /**
     * @see SampleForFieldToString#toString()
     */
    @Override
    public void visitEnd() {
        if (!isInterface && !toStringExists) {
            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, TO_STRING_METHOD_NAME, TO_STRING_METHOD_DESC, null, null);
            if (mv != null) {
                mv.visitCode();

                // sb = new StringBuilder()
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

                // sb.append("com.abc.Xyz@")
                mv.visitLdcInsn(AsmTypeNameUtils.toClassName(currentOwner) + "@");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                // sb.append(System.identityHashCode(this))
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);

                // sb.append("{")
                mv.visitLdcInsn("{");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                int size = fieldList.size();
                for (int i = 0; i < size; i++) {
                    FieldDesc fieldDesc = fieldList.get(i);
                    boolean isStaticField = (fieldDesc.access() & Opcodes.ACC_STATIC) != 0;
                    String name = fieldDesc.name();
                    String desc = fieldDesc.desc();
                    if (i != 0) {
                        // sb.append(", ")
                        mv.visitLdcInsn(", ");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    }

                    // sb.append("fieldName = ")
                    mv.visitLdcInsn(name + " = ");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                    // sb.append(fieldValue)
                    if (isStaticField) {
                        mv.visitFieldInsn(GETSTATIC, currentOwner, name, desc);
                    }
                    else {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, currentOwner, name, desc);
                    }

                    Type t = Type.getType(desc);
                    String descriptor = AsmInsnUtilsForDesc.getDescForStringBuilderAppend(t);

                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
                }

                // sb.append("}")
                mv.visitLdcInsn("}");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                // sb.toString()
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }
        }
        super.visitEnd();
    }

}
