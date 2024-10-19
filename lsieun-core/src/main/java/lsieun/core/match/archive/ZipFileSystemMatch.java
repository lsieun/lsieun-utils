package lsieun.core.match.archive;

import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@FunctionalInterface
public interface ZipFileSystemMatch extends ArchiveMatch {
    Logger logger = LoggerFactory.getLogger(ZipFileSystemMatch.class);

    boolean test(Path zipPath, FileSystem zipFileSystem);

    // region static methods: FileSystem
    static ZipFileSystemMatch exists(String entry) {
        return (zipPath, zipFs) -> {
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
        return (zipPath, zipFs) -> {
            Path rootPath = zipFs.getPath("/");
            BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> true;

            try (Stream<Path> stream = Files.find(rootPath, Integer.MAX_VALUE, predicate)) {
                Iterator<Path> it = stream.sorted().iterator();
                boolean matchFlag = false;
                while (it.hasNext()) {
                    Path entryPath = it.next();
                    Path relativePath = rootPath.relativize(entryPath);
                    String entry = relativePath.toString();
                    boolean flag = match.test(zipPath, zipFs, entry);

                    logger.debug(() -> String.format("[%s] %s", flag ? "MATCHED" : "MISMATCHED", entry));

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
