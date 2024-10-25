package lsieun.core.sam.match.archive;

import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.bytes.ByteArrayMatch;
import lsieun.core.sam.match.text.TextMatch;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@FunctionalInterface
public interface ZipEntryMatch extends ArchiveMatch {

    boolean test(Path zipPath, FileSystem zipFileSystem, String entry);

    LogicAssistant<ZipEntryMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), ZipEntryMatch.class);

    // region static methods: Text
    static ZipEntryMatch byText(TextMatch match) {
        return (zipPath, zipFs, entry) -> {
            boolean flag = match.test(entry);
            if (flag) {
                Logger logger = LoggerFactory.getLogger(ZipEntryMatch.class);
                logger.debug(() -> String.format("[MATCHED] %s - %s", zipPath, entry));
            }
            return flag;
        };
    }
    // endregion

    // region static methods: Bytes
    static ZipEntryMatch byBytes(ByteArrayMatch match) {
        return (zipPath, zipFs, entry) -> {
            try {
                Path path = zipFs.getPath(entry);
                byte[] bytes = Files.readAllBytes(path);
                return match.test(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
    // endregion
}
