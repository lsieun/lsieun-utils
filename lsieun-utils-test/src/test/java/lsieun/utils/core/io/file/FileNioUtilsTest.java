package lsieun.utils.core.io.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class FileNioUtilsTest {
    @Test
    void testCopy() throws IOException {
        Path sourcePath = Path.of("D:\\git-repo\\lsieun-utils\\src\\main\\java\\lsieun\\utils\\archive\\ZipFindNioUtils.java");
        Path targetPath = Path.of("D:\\git-repo\\lsieun-utils-asm\\src\\main\\java\\lsieun\\utils\\archive\\ZipFindNioUtils.java");
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}