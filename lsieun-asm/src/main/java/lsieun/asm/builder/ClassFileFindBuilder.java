package lsieun.asm.builder;

import lsieun.asm.common.analysis.ClassFileFindUtils;
import lsieun.asm.common.analysis.ClassFileMatchUtils;
import lsieun.asm.common.analysis.MultipleClassFileFindUtils;
import lsieun.asm.match.MatchItem;
import lsieun.asm.sam.chain.*;
import lsieun.asm.sam.match.ClassInfoMatch;
import lsieun.asm.sam.match.FieldInfoMatch;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.ds.pair.Pair;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.text.TextMatch;
import lsieun.core.sam.chain.AddArchiveEntryNameMatchWithVarArgs;
import lsieun.core.sam.chain.AddDirFromWithMaxDepthAndQuick;
import lsieun.core.sam.chain.ToRun;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ClassFileFindBuilder {
    static AddDirFromWithMaxDepthAndQuick<AddArchiveEntryNameMatchWithVarArgs<ToRun>> byEntryName() {
        return (dirPath, maxDepth, quick) -> (textMatches) -> () -> {
            TextMatch zipEntryNameMatch = TextMatch.LOGIC.and(true, textMatches);
            findByEntryName(dirPath, maxDepth, quick, zipEntryNameMatch);
        };
    }

    private static void findByEntryName(Path dirPath, int maxDepth, boolean quick, TextMatch zipEntryNameMatch) {
        List<Path> jarList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".jar");
        List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, zipEntryNameMatch, quick);
        MultipleClassFileFindUtils.print(pairList);
    }


    static AddDirFromWithMaxDepthAndQuick<AddArchiveEntryNameMatchWithVarArgs<AddClassInfoMatchWithVarArgs<AddFieldMatch<ToRun>>>> byField() {
        return (dirPath, maxDepth, quick) -> zipEntryMatches -> classMatches -> fieldMatch -> () -> {
            TextMatch zipEntryMatch = TextMatch.LOGIC.and(true, zipEntryMatches);
            ClassInfoMatch classMatch = ClassInfoMatch.LOGIC.and(true, classMatches);

            findByField(dirPath, maxDepth, quick, zipEntryMatch, classMatch, fieldMatch);
        };
    }

    private static void findByField(Path dirPath, int maxDepth, boolean quick,
                                    TextMatch zipEntryMatch,
                                    ClassInfoMatch classMatch,
                                    FieldInfoMatch fieldMatch) {
        List<Path> jarList = dir2JarList(dirPath, maxDepth);
        Function<byte[], List<MatchItem>> func = bytes -> ClassFileFindUtils.findField(bytes, fieldMatch);
        Function<Path, List<MatchItem>> func2 = (jarPath) ->
                jar2MatchItemList(jarPath, quick, zipEntryMatch, classMatch, func);
        List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, func2);
        MultipleClassFileFindUtils.print(pairList);
    }

    static AddDirFromWithMaxDepthAndQuick<AddArchiveEntryNameMatchWithVarArgs<AddClassInfoMatchWithVarArgs<AddMethodMatch<ToRun>>>> byMethod() {
        return (dirPath, maxDepth, quick) -> zipEntryMatches -> classMatches -> methodMatch -> () -> {

            TextMatch zipEntryMatch = TextMatch.LOGIC.and(true, zipEntryMatches);
            ClassInfoMatch classMatch = ClassInfoMatch.LOGIC.and(true, classMatches);

            findByMethod(dirPath, maxDepth, quick, zipEntryMatch, classMatch, methodMatch);
        };
    }

    private static void findByMethod(Path dirPath, int maxDepth, boolean quick,
                                     TextMatch zipEntryMatch,
                                     ClassInfoMatch classMatch,
                                     MethodInfoMatch methodMatch) {
        List<Path> jarList = dir2JarList(dirPath, maxDepth);
        Function<byte[], List<MatchItem>> func = bytes -> ClassFileFindUtils.findMethod(bytes, methodMatch);
        Function<Path, List<MatchItem>> func2 = (jarPath) ->
                jar2MatchItemList(jarPath, quick, zipEntryMatch, classMatch, func);
        List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, func2);
        MultipleClassFileFindUtils.print(pairList);
    }

    /**
     * <h2>Code Sample</h2>
     * <code>
     * <pre>
     * Path dirPath = Path.of(&quot;...&quot;);
     * InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.byMethodName(&quot;test&quot;);
     * ClassFileFindBuilder.byInsn()
     *         .withFromDir(dirPath, 1, false)
     *         .withEntryName()
     *         .withClassMatch()
     *         .withMethodMatch()
     *         .withInsnMatch(insnInvokeMatch, true)
     *         .run();
     * </pre>
     * </code>
     */
    static AddDirFromWithMaxDepthAndQuick<AddArchiveEntryNameMatchWithVarArgs<
            AddClassInfoMatchWithVarArgs<AddMethodMatchWithVarArgs<AddInsnMatchWithUnique<ToRun>>>>> byInsn() {
        return (dirPath, maxDepth, quick) -> zipEntryMatches -> classMatches -> methodMatches ->
                (insnMatch, deduplicate) -> () -> {
                    LogicAssistant<TextMatch> zipEntryLogic = TextMatch.LOGIC;
                    TextMatch zipEntryMatch = zipEntryLogic.and(true, zipEntryMatches);

                    ClassInfoMatch classMatch = ClassInfoMatch.LOGIC.and(true, classMatches);
                    MethodInfoMatch methodMatch = MethodInfoMatch.LOGIC.and(true, methodMatches);

                    findByInsn(dirPath, maxDepth, quick, zipEntryMatch, classMatch, methodMatch, insnMatch, deduplicate);
                };
    }

    private static void findByInsn(Path dirPath, int maxDepth, boolean quick,
                                   TextMatch zipEntryMatch, ClassInfoMatch classMatch,
                                   MethodInfoMatch methodMatch,
                                   InsnInvokeMatch insnMatch, boolean deduplicate) {
        List<Path> jarList = dir2JarList(dirPath, maxDepth);
        Function<byte[], List<MatchItem>> func = bytes ->
                ClassFileFindUtils.findInsnByInvokeXxx(bytes, methodMatch, insnMatch, deduplicate);
        Function<Path, List<MatchItem>> func2 = (jarPath) ->
                jar2MatchItemList(jarPath, quick, zipEntryMatch, classMatch, func);
        List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, func2);
        MultipleClassFileFindUtils.print(pairList);
    }

    private void findByInsnExample() {
        Path dirPath = Path.of("...");
        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.byMethodName("test");
        ClassFileFindBuilder.byInsn()
                .withFromDir(dirPath, 1, false)
                .withEntryName()
                .withClassMatch()
                .withMethodMatch()
                .withInsnMatch(insnInvokeMatch, true)
                .run();
    }

    private static List<Path> dir2JarList(Path dirPath, int maxDepth) {
        return DirNioUtils.findFileListInDirByExt(dirPath, ".jar");
    }

    private static List<Pair<Path, List<MatchItem>>> jarList2PairList(@NotNull List<Path> jarList,
                                                                      TextMatch textMatch,
                                                                      boolean quick) {
        if (jarList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Pair<Path, List<MatchItem>>> resultList = new ArrayList<>();
        for (Path jarPath : jarList) {
            List<MatchItem> matchItemList = jar2MatchItemList(jarPath, textMatch, quick);
            if (matchItemList.isEmpty()) {
                continue;
            }

            resultList.add(new Pair<>(jarPath, matchItemList));
        }
        return resultList;
    }

    private static List<Pair<Path, List<MatchItem>>> jarList2PairList(@NotNull List<Path> jarList,
                                                                      Function<Path, List<MatchItem>> func) {
        if (jarList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Pair<Path, List<MatchItem>>> resultList = new ArrayList<>();
        for (Path jarPath : jarList) {
            List<MatchItem> matchItemList = func.apply(jarPath);
            if (matchItemList.isEmpty()) {
                continue;
            }

            resultList.add(new Pair<>(jarPath, matchItemList));
        }
        return resultList;
    }

    private static List<MatchItem> jar2MatchItemList(Path jarPath, TextMatch textMatch, boolean quick) {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
            // (1) entry
            Path rootPath = path.getRoot();
            Path relativizedPath = rootPath.relativize(path);
            String entry = relativizedPath.toString();

            // (2) logic
            LogicAssistant<TextMatch> logic = TextMatch.LOGIC;
            TextMatch match = logic.and(true, textMatch, TextMatch.endsWith(".class"));

            // (3) test
            return match.test(entry);
        };
        return jar2MatchItemList(jarPath, predicate, quick);
    }

    private static List<MatchItem> jar2MatchItemList(Path jarPath, BiPredicate<Path, BasicFileAttributes> predicate, boolean quick) {
        // (1) uri
        URI zipUri = URI.create("jar:" + jarPath.toUri());

        // (2) env
        Map<String, String> env = new HashMap<>(1);
        env.put("create", "false");

        // (3) use
        List<MatchItem> resultList = new ArrayList<>();
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
                    int index = entry.lastIndexOf('.');
                    String internalName = entry.substring(0, index);
                    MatchItem matchItem = MatchItem.ofType(internalName);
                    resultList.add(matchItem);

                    if (quick) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }

    private static <T> List<MatchItem> jar2MatchItemList(Path jarPath, boolean quick,
                                                         TextMatch jarEntryMatch,
                                                         ClassInfoMatch classMatch,
                                                         Function<byte[], List<MatchItem>> func) {
        // (1) uri
        URI zipUri = URI.create("jar:" + jarPath.toUri());

        // (2) env
        Map<String, String> env = new HashMap<>(1);
        env.put("create", "false");

        // (3) use
        List<MatchItem> resultList = new ArrayList<>();
        try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
            // (1) root
            Path rootPath = zipFs.getPath("/");

            // (2) predicate: .class
            LogicAssistant<TextMatch> logic = TextMatch.LOGIC;
            BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
                Path relativePath = path.getRoot().relativize(path);
                String entry = relativePath.toString();
                return logic.and(true, jarEntryMatch, TextMatch.endsWith(".class")).test(entry);
            };

            // (3) find
            try (Stream<Path> stream = Files.find(rootPath, Integer.MAX_VALUE, predicate)) {
                Iterator<Path> it = stream.sorted().iterator();
                while (it.hasNext()) {
                    Path entryPath = it.next();
                    if (Files.isDirectory(entryPath)) {
                        continue;
                    }

                    byte[] bytes = Files.readAllBytes(entryPath);

                    boolean flag = ClassFileMatchUtils.matchClassInfo(bytes, classMatch);
                    if (!flag) {
                        continue;
                    }

                    List<MatchItem> matchItemList = func.apply(bytes);
                    resultList.addAll(matchItemList);

                    if (quick) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }
}
