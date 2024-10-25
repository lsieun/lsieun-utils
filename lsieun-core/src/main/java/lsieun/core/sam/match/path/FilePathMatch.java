package lsieun.core.sam.match.path;

import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.archive.ZipFileSystemMatch;
import lsieun.core.sam.match.bytes.ByteArrayMatch;
import lsieun.core.sam.match.text.TextMatch;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface FilePathMatch extends Predicate<Path> {
    boolean test(Path path);

    LogicAssistant<FilePathMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), FilePathMatch.class);

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
                return zipFileSystemMatch.test(path, zipFs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
    // endregion

    static LogicAssistant<FilePathMatch> logic() {
        return LogicAssistant.of(MethodHandles.lookup(), FilePathMatch.class);
    }
}
