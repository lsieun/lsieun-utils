package lsieun.core.sam.chain;

@FunctionalInterface
public interface AddDirWithMultipleArgs<T> {
    T withDir(String first, String... more);
}
