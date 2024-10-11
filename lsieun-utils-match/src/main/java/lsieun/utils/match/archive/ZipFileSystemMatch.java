package lsieun.utils.match.archive;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@FunctionalInterface
public interface ZipFileSystemMatch {
    boolean test(FileSystem zipFileSystem);

    // region static methods: FileSystem
    static ZipFileSystemMatch exists(String entry) {
        return zipFs -> {
            Path path = zipFs.getPath(entry);
            return Files.exists(path);
        };
    }

    static ZipFileSystemMatch existsByClassName(String className) {
        String entry = className.replace('.', '/') + ".class";
        return exists(entry);
    }

    static ZipFileSystemMatch existsByClass(Class<?> clazz) {
        String entry = clazz.getTypeName().replace('.', '/') + ".class";
        return exists(entry);
    }
    // endregion

    // region static methods: ZipEntryMatch
    static ZipFileSystemMatch byZipEntry(ZipEntryMatch match) {
        return byZipEntry(match, true);
    }

    static ZipFileSystemMatch byZipEntry(ZipEntryMatch match, boolean quick) {
        return zipFs -> {
            Path dirPath = zipFs.getPath("/");
            BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> true;

            try (Stream<Path> stream = Files.find(dirPath, Integer.MAX_VALUE, predicate)) {
                List<String> list = stream
                        .map(Path::toString)
                        .sorted()
                        .toList();
                int size = list.size();
                boolean matchFlag = false;
                for (int i = 0; i < size; i++) {
                    String entry = list.get(i);
                    boolean flag = match.test(zipFs, entry);
                    if (quick && flag) {
                        return true;
                    }
                    else {
                        matchFlag |= flag;
                    }
                }
                return matchFlag;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
    // endregion
}
