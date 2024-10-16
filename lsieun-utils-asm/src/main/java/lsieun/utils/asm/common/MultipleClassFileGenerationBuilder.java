package lsieun.utils.asm.common;

import lsieun.utils.annotation.type.asm.AsmMatchGeneration;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.core.io.dir.DirNioUtils;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;

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
