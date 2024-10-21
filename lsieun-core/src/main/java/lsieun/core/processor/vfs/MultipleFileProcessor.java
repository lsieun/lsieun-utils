package lsieun.core.processor.vfs;

import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.nio.file.FileSystem;

public interface MultipleFileProcessor {
    Logger logger = LoggerFactory.getLogger(MultipleFileProcessor.class);

    static void builder(FileSystem fs) {

    }

//    static AddDir builder() {
//        return (dirPath, maxDepth) -> filePathMatch -> singleFileProcessor -> {
//            process(dirPath, maxDepth, filePathMatch, singleFileProcessor);
//        };
//    }
//
//    private static void process(Path dirPath, int maxDepth, FilePathMatch filePathMatch, SingleFileProcessor singleFileProcessor) {
//        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> filePathMatch.test(path);
//        List<Path> fileList = DirNioUtils.findFileListInDir(dirPath, maxDepth, predicate);
//        int size = fileList.size();
//        logger.debug(() -> String.format("File List Size: %d", size));
//        if (size == 0) {
//            return;
//        }
//        for (int i = 0; i < size; i++) {
//            Path filePath = fileList.get(i);
//            logger.debug(() -> String.format("Processing file: %s", filePath));
//            singleFileProcessor.accept(filePath);
//        }
//    }
//
//    interface AddDir {
//        AddFilePathMatch withDir(Path dirPath, int maxDepth);
//    }
//
//    interface AddFilePathMatch {
//        AddSingleFileProcess withFilePathMatch(FilePathMatch filePathMatch);
//    }
//
//    interface AddSingleFileProcess {
//        void withSingleFileProcess(SingleFileProcessor singleFileProcessor);
//    }
}
