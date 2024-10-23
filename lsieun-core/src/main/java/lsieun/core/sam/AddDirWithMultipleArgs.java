package lsieun.core.sam;

@FunctionalInterface
public interface AddDirWithMultipleArgs<T> {
    T withDir(String first, String... more);
}
