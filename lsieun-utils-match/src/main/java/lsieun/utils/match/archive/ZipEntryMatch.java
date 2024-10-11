package lsieun.utils.match.archive;

import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import lsieun.utils.match.bytes.ByteArrayMatch;
import lsieun.utils.match.text.TextMatch;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ZipEntryMatch {

    boolean test(FileSystem zipFileSystem, String entry);

    // region static methods: Text
    static ZipEntryMatch byText(TextMatch match) {
        return (zipFs, entry) -> {
            boolean flag = match.test(entry);
            if (flag) {
                Logger logger = LoggerFactory.getLogger(ZipEntryMatch.class);
                logger.debug(entry + " matched " + flag);
            }
            return flag;
        };
    }
    // endregion

    // region static methods: Bytes
    static ZipEntryMatch byBytes(ByteArrayMatch match) {
        return (zipFs, entry) -> {
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
