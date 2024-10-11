package lsieun.utils.core.io.dir;

import lsieun.utils.annotation.method.MethodParamExample;
import lsieun.utils.core.io.file.FileFormatUtils;
import lsieun.utils.core.io.file.FileOperation;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirNioUtils {
    private static final Logger logger = LoggerFactory.getLogger(DirNioUtils.class);

    public static List<Path> findFileListInDirByExt(Path dirPath,
                                                    @MethodParamExample({".md", ".java"}) String ext) throws IOException {
        return findFileListInDirByExt(dirPath, Integer.MAX_VALUE, ext);
    }

    public static List<Path> findFileListInDirByExt(Path dirPath,
                                                    int maxDepth,
                                                    @MethodParamExample({".md", ".java"}) String ext) throws IOException {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attrs) ->
                attrs.isRegularFile() && path.getFileName().toString().endsWith(ext);
        return findFileListInDir(dirPath, maxDepth, predicate);
    }

    public static List<Path> findFileListInDir(Path dirPath, int maxDepth,
                                               BiPredicate<Path, BasicFileAttributes> predicate) throws IOException {
        Objects.requireNonNull(dirPath);
        Objects.requireNonNull(predicate);

        // (1) not exist
        if (!Files.exists(dirPath)) {
            return Collections.emptyList();
        }

        // (2) check dir
        if (!Files.isDirectory(dirPath)) {
            return Collections.emptyList();
        }

        // (3) find
        try (Stream<Path> stream = Files.find(dirPath, maxDepth, predicate)) {
            return stream.collect(Collectors.toList());
        }
    }

    public static void addFileNamePrefixInDirectory(Path dirPath,
                                                    @MethodParamExample({".java"}) String ext,
                                                    @MethodParamExample({"bytebuddy-"}) String prefix) throws IOException {
        List<Path> pathList = findFileListInDirByExt(dirPath, ext);
        int size = pathList.size();
        for (int i = 0; i < size; i++) {
            Path path = pathList.get(i);
            Path newFilePath = addFileNamePrefix(path, prefix);
            if (!path.equals(newFilePath)) {
                logger.debug(() -> FileFormatUtils.format(path, newFilePath, FileOperation.MOVE));
            }
        }
    }

    private static Path addFileNamePrefix(Path file, String prefix) throws IOException {
        // (1) not exist
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("file not exist: " + file);
        }

        // (2) check file name
        Path fileName = file.getFileName();
        if (fileName.toString().toLowerCase().startsWith(prefix.toLowerCase())) {
            return file;
        }

        // (3) move file
        Path parent = file.getParent();
        Path newPath = Paths.get(parent.toString(), prefix + fileName);
        Files.move(file, newPath);
        return newPath;
    }
}
