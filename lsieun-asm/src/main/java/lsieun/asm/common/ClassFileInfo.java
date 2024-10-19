package lsieun.asm.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class ClassFileInfo {
    private final byte[] bytes;
    public final String owner;
    public final int access;

    public ClassFileInfo(byte[] bytes) {
        this.bytes = bytes;

        ClassReader cr = new ClassReader(bytes);
        this.owner = cr.getClassName();
        this.access = cr.getAccess();
    }

    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }


}
