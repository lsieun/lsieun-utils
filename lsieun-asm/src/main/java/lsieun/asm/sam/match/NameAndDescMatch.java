package lsieun.asm.sam.match;

@FunctionalInterface
public interface NameAndDescMatch {
    boolean test(String name, String desc);
}
