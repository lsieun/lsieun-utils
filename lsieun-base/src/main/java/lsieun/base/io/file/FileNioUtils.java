package lsieun.base.io.file;

import lsieun.annotation.type.UtilityClass;
import lsieun.base.coll.ListUtils;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@UtilityClass
public class FileNioUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileNioUtils.class);

    private FileNioUtils() {
        throw new UnsupportedOperationException();
    }

    // region copy
    public static void backup(Path path, String suffix) throws IOException {

        Path dirPath = path.getParent();
        Path fileName = path.getFileName();
        Path newFilePath = dirPath.resolve(fileName.toString() + suffix);

        if (!Files.exists(dirPath)) {
            throw new IllegalArgumentException("path is not exist: " + dirPath);
        }

        if (Files.exists(newFilePath)) {
            copy(newFilePath, path);
        }
        else if (Files.exists(path)) {
            copy(path, newFilePath);
        }
        else {
            throw new IllegalArgumentException("path is not exist: " + path);
        }
    }

    public static void copy(Path srcPath, Path dstPath) throws IOException {
        if (!Files.exists(srcPath)) {
            throw new IllegalArgumentException("Source path does not exist: " + srcPath);
        }
        if (!Files.exists(dstPath)) {
            Path parent = dstPath.getParent();
            Files.createDirectories(parent);
        }

        Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
        logger.info(() -> FileFormatUtils.format(srcPath, dstPath, FileOperation.COPY));
    }

    public static void copyFileList2Dir(List<Path> fileList, Path dstDirPath) throws IOException {
        if (ListUtils.isNullOrEmpty(fileList)) {
            return;
        }

        for (Path filepath : fileList) {
            Path fileName = filepath.getFileName();
            Path newFilePath = dstDirPath.resolve(fileName);
            copy(filepath, newFilePath);
        }
    }
    // endregion
}
