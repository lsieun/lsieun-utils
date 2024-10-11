package lsieun.utils.asm.visitor;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.text.RegexUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class ClassRegexVisitor extends ClassVisitor implements Opcodes {
    // 开始前，传入的参数
    public final String[] includes;
    public final String[] excludes;

    // 结束后，输出的数据
    public final List<MatchItem> resultList = new ArrayList<>();


    public ClassRegexVisitor(ClassVisitor cv, String[] includes, String[] excludes) {
        super(MyAsmConst.ASM_API_VERSION, cv);
        this.includes = includes;
        this.excludes = excludes;
    }

    public boolean isAppropriate(String item) {
        if (RegexUtils.matches(item, excludes, false)) {
            return false;
        }
        return RegexUtils.matches(item, includes, true);
    }

    public void addResult(MatchItem item) {
        if (item == null) {
            return;
        }

        if (!resultList.contains(item)) {
            resultList.add(item);
        }
    }

    protected void printMethodParameters(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        if (mv == null) return;

        boolean isStaticMethod = ((access & ACC_STATIC) != 0);
        int slotIndex = isStaticMethod ? 0 : 1;

        printMessage(mv, "Method Enter: " + name + desc);

        Type methodType = Type.getMethodType(desc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        for (Type t : argumentTypes) {
            int sort = t.getSort();
            int size = t.getSize();
            String descriptor = t.getDescriptor();

            int opcode = t.getOpcode(ILOAD);
            mv.visitVarInsn(opcode, slotIndex);

            if (sort == Type.BOOLEAN) {
                printBoolean(mv);
            }
            else if (sort == Type.CHAR) {
                printChar(mv);
            }
            else if (sort == Type.BYTE || sort == Type.SHORT || sort == Type.INT) {
                printInt(mv);
            }
            else if (sort == Type.FLOAT) {
                printFloat(mv);
            }
            else if (sort == Type.LONG) {
                printLong(mv);
            }
            else if (sort == Type.DOUBLE) {
                printDouble(mv);
            }
            else if (sort == Type.OBJECT && "Ljava/lang/String;".equals(descriptor)) {
                printString(mv);
            }
            else if (sort == Type.OBJECT) {
                printObject(mv);
            }
            else {
                printMessage(mv, "No Support");
            }
            slotIndex += size;
        }
    }

    private void printBoolean(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
        }
    }

    private void printChar(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false);
        }
    }

    private void printInt(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        }
    }

    private void printFloat(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
        }
    }

    private void printLong(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
        }
    }

    private void printDouble(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
        }
    }

    private void printString(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

    private void printObject(MethodVisitor mv) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
        }
    }

    private void printMessage(MethodVisitor mv, String str) {
        if (mv != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(str);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
}
