package lsieun.core.sam.chain;

import java.nio.file.Path;

@FunctionalInterface
public interface AddDirTo<T> {
    T withTargetDir(Path targetDir);
}
