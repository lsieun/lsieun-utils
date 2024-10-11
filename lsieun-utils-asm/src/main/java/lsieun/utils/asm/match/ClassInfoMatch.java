package lsieun.utils.asm.match;


public interface ClassInfoMatch {
    boolean test(int version, int access, String name, String signature, String superName, String[] interfaces);
}
