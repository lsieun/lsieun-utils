package lsieun.utils.match.path;

import lsieun.utils.core.io.dir.DirNioUtils;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;

public interface MultipleFileProcessor {
    Logger logger = LoggerFactory.getLogger(MultipleFileProcessor.class);

    static AddDir builder() {
        return (dirPath, maxDepth) -> filePathMatch -> singleFileProcessor -> {
            process(dirPath, maxDepth, filePathMatch, singleFileProcessor);
        };
    }

    private static void process(Path dirPath, int maxDepth, FilePathMatch filePathMatch, SingleFileProcessor singleFileProcessor) {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> filePathMatch.test(path);
        List<Path> fileList = DirNioUtils.findFileListInDir(dirPath, maxDepth, predicate);
        int size = fileList.size();
        logger.debug(() -> String.format("File List Size: %d", size));
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            Path filePath = fileList.get(i);
            logger.debug(() -> String.format("Processing file: %s", filePath));
            singleFileProcessor.accept(filePath);
        }
    }

    interface AddDir {
        AddFilePathMatch withDir(Path dirPath, int maxDepth);
    }

    interface AddFilePathMatch {
        AddSingleFileProcess withFilePathMatch(FilePathMatch filePathMatch);
    }

    interface AddSingleFileProcess {
        void withSingleFileProcess(SingleFileProcessor singleFileProcessor);
    }
}
