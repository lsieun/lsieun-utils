package lsieun.asm.builder;

import lsieun.annotation.mind.todo.ToDo;
import lsieun.asm.common.analysis.ClassFileMatchUtils;
import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.chain.*;
import lsieun.asm.sam.match.ClassInfoMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.text.TextMatch;
import lsieun.core.sam.chain.AddArchiveEntryNameMatchWithVarArgs;
import lsieun.core.sam.chain.AddDirFromWithMaxDepth;
import lsieun.core.sam.chain.AddFilePathMatchWithVarArgs;
import lsieun.core.sam.chain.ToRun;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static lsieun.asm.common.transformation.InsnProcess.getCandidateJarList;
import static lsieun.asm.common.transformation.InsnProcess.processInsn;

@ToDo({
        "在进行修改之前，需要先判断相应的 instruction 是否存在"
})
public interface ClassFileModifyBuilder {
    Logger logger = LoggerFactory.getLogger(ClassFileModifyBuilder.class);

    static AddDirFromWithMaxDepth<
            AddFilePathMatchWithVarArgs<
                    AddArchiveEntryNameMatchWithVarArgs<
                            AddClassInfoMatchWithVarArgs<
                                    AddMethodMatchWithVarArgs<
                                            AddInsnMatch<
                                                    AddInsnInvokeConsumer<
                                                            ToRun>>>>>>> traceInsnInvoke() {
        return (dirPath, maxDepth) -> filePathMatches -> zipEntryMatchs -> classMatches -> methodMatches ->
                insnInvokeMatch -> insnInvokeConsumer -> () ->
                {
                    List<Path> jarList = getCandidateJarList(dirPath, maxDepth, filePathMatches);
                    processInsn(jarList, zipEntryMatchs,
                            classMatches, methodMatches,
                            insnInvokeMatch, insnInvokeConsumer);
                };

    }

    static AddDirFromWithMaxDepth<AddFilePathMatchWithVarArgs<AddArchiveEntryNameMatchWithVarArgs<
            AddClassInfoMatchWithVarArgs<AddMethodMatch<ToRun>>>>> traceInsn() {
        return (dirPath, maxDepth) -> filePathMatches -> zipEntryMatches -> classMatches -> methodMatch -> () ->
        {
            List<Path> jarList = getCandidateJarList(dirPath, maxDepth, filePathMatches);
            processMethod(jarList, zipEntryMatches, classMatches, methodMatch);
        };

    }

    static void processMethod(List<Path> jarList, TextMatch[] zipEntryMatches,
                              ClassInfoMatch[] classMatches, MethodInfoMatch methodMatch) {
        // (1) match: zip entry
        LogicAssistant<TextMatch> textLogic = TextMatch.LOGIC;
        TextMatch zipEntryMatch = textLogic.and(TextMatch.endsWith(".class"), zipEntryMatches);

        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
            // (1) entry
            Path rootPath = path.getRoot();
            Path relativizedPath = rootPath.relativize(path);
            String entry = relativizedPath.toString();

            // (2) test
            return zipEntryMatch.test(entry);
        };

        // (2) match: class
        ClassInfoMatch classMatch = ClassInfoMatch.LOGIC.and(true, classMatches);


        // (3) transform jar
        int size = jarList.size();
        logger.debug(() -> String.format("Jar List Size: %d", size));
        for (int i = 0; i < size; i++) {
            int index = i + 1;
            Path jarPath = jarList.get(i);
            logger.debug(() -> MatchFormat.format(index, MatchState.MATCHING, jarPath));

            // (1) uri
            URI zipUri = URI.create("jar:" + jarPath.toUri());

            // (2) env
            Map<String, String> env = new HashMap<>(1);
            env.put("create", "false");

            // (3) use
            try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
                Path rootPath = zipFs.getPath("/");


                try (Stream<Path> stream = Files.find(rootPath, Integer.MAX_VALUE, predicate)) {
                    Iterator<Path> it = stream.sorted().iterator();
                    while (it.hasNext()) {
                        Path entryPath = it.next();
                        if (Files.isDirectory(entryPath)) {
                            continue;
                        }
                        Path relativePath = rootPath.relativize(entryPath);
                        String entry = relativePath.toString();

                        byte[] bytes = Files.readAllBytes(entryPath);

                        boolean classMatchFlag = ClassFileMatchUtils.matchClassInfo(bytes, classMatch);
                        if (!classMatchFlag) {
                            logger.debug(() -> MatchFormat.format(MatchState.SKIP, entryPath, "class match fails"));
                            continue;
                        }

                        boolean insnMatchFlag = ClassFileMatchUtils.matchMethodInfo(bytes, methodMatch);
                        if (!insnMatchFlag) {
                            logger.debug(() -> MatchFormat.format(MatchState.SKIP, entryPath, "method match fails"));
                            continue;
                        }

                        logger.debug(() -> MatchFormat.format(MatchState.MATCHED, entryPath));
                        byte[] newBytes = ClassFileModifyUtils.traceInsn(bytes, methodMatch);
                        Files.write(entryPath, newBytes);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
