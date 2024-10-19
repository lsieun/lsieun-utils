package lsieun.asm.common.transformation;

import java.nio.file.Path;
import java.util.List;

import static lsieun.asm.common.transformation.InsnProcess.getCandidateJarList;
import static lsieun.asm.common.transformation.InsnProcess.processInsn;

public interface ClassFileModifyBuilder {
    static InsnProcess.AddDir traceInsnInvoke() {
        return (dirPath, maxDepth) -> filePathMatches ->
                zipEntryMatchs -> classMatches -> methodMatches ->
                        insnInvokeMatch -> insnInvokeConsumer ->
                        {
                            List<Path> jarList = getCandidateJarList(dirPath, maxDepth, filePathMatches);
                            processInsn(jarList, zipEntryMatchs,
                                    classMatches, methodMatches,
                                    insnInvokeMatch, insnInvokeConsumer);
                        };

    }
}
