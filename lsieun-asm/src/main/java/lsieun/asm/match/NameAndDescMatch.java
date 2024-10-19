package lsieun.asm.match;

@FunctionalInterface
public interface NameAndDescMatch {
    boolean test(String name, String desc);
}
