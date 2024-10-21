package lsieun.asm.common.transformation;

import lsieun.asm.common.analysis.ClassFileMatchUtils;
import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.match.ClassInfoMatch;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.match.format.MatchFormat;
import lsieun.asm.match.format.MatchState;
import lsieun.base.coll.ListUtils;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import lsieun.core.match.LogicAssistant;
import lsieun.core.match.path.FilePathMatch;
import lsieun.core.match.text.TextMatch;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface InsnProcess {
    Logger logger = LoggerFactory.getLogger(InsnProcess.class);

    static List<Path> getCandidateJarList(Path dirPath, int maxDepth, FilePathMatch[] filePathMatches) {
        List<Path> jarList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".jar");
        if (ListUtils.isNullOrEmpty(jarList)) {
            return Collections.emptyList();
        }

        LogicAssistant<FilePathMatch> filePathMatchLogic = FilePathMatch.logic();
        FilePathMatch filePathMatch = filePathMatchLogic.and(true, filePathMatches);

        int jarListSize = jarList.size();
        List<Path> candidateList = new ArrayList<>();
        for (int i = 0; i < jarListSize; i++) {
            Path jarPath = jarList.get(i);

            if (!filePathMatch.test(jarPath)) {
                continue;
            }
            candidateList.add(jarPath);
        }
        return candidateList;
    }

    static void processInsn(List<Path> jarList, TextMatch[] zipEntryMatchs,
                            ClassInfoMatch[] classMatches, MethodInfoMatch[] methodMatches,
                            InsnInvokeMatch insnInvokeMatch, InsnInvokeConsumer insnInvokeConsumer) {
        // (1) match: zip entry
        LogicAssistant<TextMatch> textLogic = TextMatch.logic();
        TextMatch zipEntryMatch = textLogic.and(TextMatch.endsWith(".class"), zipEntryMatchs);

        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
            // (1) entry
            Path rootPath = path.getRoot();
            Path relativizedPath = rootPath.relativize(path);
            String entry = relativizedPath.toString();

            // (2) test
            return zipEntryMatch.test(entry);
        };

        // (2) match: class
        ClassInfoMatch classMatch = ClassInfoMatch.logic().and(true, classMatches);

        // (3) match: method
        MethodInfoMatch methodMatch = MethodInfoMatch.logic().and(true, methodMatches);

        // (4) transform jar
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

                        boolean insnMatchFlag = ClassFileMatchUtils.matchInsnInvoke(bytes, methodMatch, insnInvokeMatch);
                        if (!insnMatchFlag) {
                            logger.debug(() -> MatchFormat.format(MatchState.SKIP, entryPath, "insn match fails"));
                            continue;
                        }

                        logger.debug(() -> MatchFormat.format(MatchState.MATCHED, entryPath));
                        byte[] newBytes = ClassFileModifyUtils.modifyInsnInvoke(bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer);
                        Files.write(entryPath, newBytes);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    interface AddDir {
        AddFilePathMatch withDir(Path dirPath, int maxDepth);
    }

    interface AddFilePathMatch {
        AddZipEntryMatch withFilePathMatch(FilePathMatch... filePathMatches);
    }

    @FunctionalInterface
    interface AddZipEntryMatch {
        AddClassInfoMatch withZipEntryMatch(TextMatch... zipEntryMatchs);
    }

    @FunctionalInterface
    interface AddClassInfoMatch {
        AddMethodMatch withClassInfoMatch(ClassInfoMatch... classMatches);
    }

    @FunctionalInterface
    interface AddMethodMatch {
        AddInsnMatch withMethodMatch(MethodInfoMatch... methodMatches);
    }

    @FunctionalInterface
    interface AddInsnMatch {
        AddInsnInvokeConsumer withInsnMatch(InsnInvokeMatch insnInvokeMatch);
    }

    @FunctionalInterface
    interface AddInsnInvokeConsumer {
        void withInsnInvokeConsumer(InsnInvokeConsumer insnInvokeConsumer);
    }
}
