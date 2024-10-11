package lsieun.utils.match.path;

import lsieun.utils.match.archive.ZipFileSystemMatch;
import lsieun.utils.match.bytes.ByteArrayMatch;
import lsieun.utils.match.text.TextMatch;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface FilePathMatch {
    boolean test(Path path);


    // region text
    static FilePathMatch byFileName(TextMatch textMatch) {
        Function<Path, String> func = filepath -> filepath.getFileName().toString();
        return byText(func, textMatch);
    }

    static FilePathMatch byText(Function<Path, String> func, TextMatch textMatch) {
        return path -> textMatch.test(func.apply(path));
    }
    // endregion

    // region bytes
    static FilePathMatch byByteArray(ByteArrayMatch byteArrayMatch) {
        return path -> {
            try {
                byte[] bytes = Files.readAllBytes(path);
                return byteArrayMatch.test(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
    // endregion

    // region FileSystem
    static FilePathMatch byZipFileSystem(ZipFileSystemMatch zipFileSystemMatch) {
        return path -> {
            // (1) uri
            URI zipUri = URI.create("jar:" + path.toUri());

            // (2) env
            Map<String, String> env = new HashMap<>(1);
            env.put("create", "false");

            // (3) use
            try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
                return zipFileSystemMatch.test(zipFs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
    // endregion


}
