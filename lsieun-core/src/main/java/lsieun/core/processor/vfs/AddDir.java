package lsieun.core.processor.vfs;

public interface AddDir<T> {
    T withDir(String first, String... more);
}
