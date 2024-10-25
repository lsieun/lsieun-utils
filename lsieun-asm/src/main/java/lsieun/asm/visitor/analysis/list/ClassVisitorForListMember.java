package lsieun.asm.visitor.analysis.list;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.sam.match.MemberInfoMatch;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ClassVisitorForListMember extends ClassVisitor {
    private final MemberInfoMatch memberMatch;
    private String currentOwner;

    public ClassVisitorForListMember() {
        this(MemberInfoMatch.LOGIC.alwaysTrue());
    }

    public ClassVisitorForListMember(MemberInfoMatch memberMatch) {
        super(MyAsmConst.ASM_API_VERSION, null);
        this.memberMatch = memberMatch;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentOwner = name;
        String msg = String.format("Java %s: %s %s extends %s implements %s",
                (version - 44), Modifier.toString(access), name, superName, Arrays.toString(interfaces));
        System.out.println(msg);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (memberMatch.test(currentOwner, access, name, descriptor)) {
            String msg = String.format("    %s %s:%s", Modifier.toString(access), name, descriptor);
            System.out.println(msg);
        }

        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (memberMatch.test(currentOwner, access, name, descriptor)) {
            String msg = String.format("    %s %s:%s", Modifier.toString(access), name, descriptor);
            System.out.println(msg);
        }
        return null;
    }
}
