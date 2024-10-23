package lsieun.asm.common.generation;

import lsieun.annotation.type.asm.AsmMatchGeneration;
import lsieun.asm.common.ClassFileAnnotationUtils;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.match.MatchState;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MultipleClassFileGenerationBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MultipleClassFileGenerationBuilder.class);

    public static void generateMatch(Path dirPath, int maxDepth) throws IOException {
        List<Path> classFileList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".class");
        int size = classFileList.size();
        for (int i = 0; i < size; i++) {
            int index = i + 1;
            Path classFilePath = classFileList.get(i);
            byte[] bytes = Files.readAllBytes(classFilePath);
            boolean exists = ClassFileAnnotationUtils.checkExists(bytes, AsmMatchGeneration.class);
            if (exists) {
                logger.info(() -> MatchFormat.format(index, MatchState.MATCHED, classFilePath.toUri().toString()));
                ClassFileGenerationUtils.generateMatch(classFilePath);
            }
            else {
                logger.debug(() -> MatchFormat.format(index, MatchState.SKIP, classFilePath.toUri().toString()));
            }
        }
    }
}
