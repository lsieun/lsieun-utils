package lsieun.utils.asm.common;

import jakarta.annotation.Nonnull;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.LogicAssistant;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.ds.pair.Pair;
import lsieun.utils.core.io.dir.DirNioUtils;
import lsieun.utils.match.text.TextMatch;

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
    static FindByEntryName.AddDir byEntryName() {
        return (dirPath, maxDepth, quick) ->
                (textMatch) ->
                        () -> {
                            List<Path> jarList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".jar");
                            List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, textMatch, quick);
                            MultipleClassFileFindUtils.print(pairList);
                        };
    }

    static FindByMethod.AddDir byMethod() {
        return (dirPath, maxDepth, quick) ->
                methodMatch ->
                        () -> {
                            List<Path> jarList = dir2JarList(dirPath, maxDepth);
                            Function<byte[], List<MatchItem>> func = bytes -> ClassFileFindUtils.findMethod(bytes, methodMatch);
                            Function<Path, List<MatchItem>> func2 = (jarPath) ->
                                    jar2MatchItemList(jarPath, quick, func);
                            List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, func2);
                            MultipleClassFileFindUtils.print(pairList);
                        };
    }

    static FindByInsn.AddDir byInsn() {
        return (dirPath, maxDepth, quick) -> methodMatch -> (insnMatch, deduplicate) -> () -> {
            List<Path> jarList = dir2JarList(dirPath, maxDepth);
            Function<byte[], List<MatchItem>> func = bytes -> ClassFileFindUtils.findInsnByInvokeXxx(bytes, methodMatch, insnMatch, deduplicate);
            Function<Path, List<MatchItem>> func2 = (jarPath) ->
                    jar2MatchItemList(jarPath, quick, func);
            List<Pair<Path, List<MatchItem>>> pairList = jarList2PairList(jarList, func2);
            MultipleClassFileFindUtils.print(pairList);
        };
    }

    interface FindByEntryName {
        @FunctionalInterface
        interface AddDir {
            AddEntryNameMatch withDir(Path dirPath, int maxDepth, boolean quick);
        }

        @FunctionalInterface
        interface AddEntryNameMatch {
            ToPrint withTextMatch(TextMatch textMatch);
        }
    }

    @FunctionalInterface
    interface ToPrint {
        void print();
    }

    interface FindByMethod {
        interface AddDir {
            AddMethodMatch withDir(Path dirPath, int maxDepth, boolean quick);
        }

        interface AddMethodMatch {
            ToPrint withMethodMatch(MethodInfoMatch methodMatch);
        }
    }

    interface FindByInsn {
        @FunctionalInterface
        interface AddDir {
            AddMethodMatch withDir(Path dirPath, int maxDepth, boolean quick);
        }

        @FunctionalInterface
        interface AddMethodMatch {
            AddInsnMatch withMethodMatch(MethodInfoMatch methodMatch);
        }

        @FunctionalInterface
        interface AddInsnMatch {
            ToPrint withInsnMatch(InsnInvokeMatch insnInvokeMatch, boolean deduplicate);
        }
    }

    static List<Path> dir2JarList(Path dirPath, int maxDepth) {
        return DirNioUtils.findFileListInDirByExt(dirPath, ".jar");
    }

    static List<Pair<Path, List<MatchItem>>> jarList2PairList(@Nonnull List<Path> jarList,
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

    static List<Pair<Path, List<MatchItem>>> jarList2PairList(@Nonnull List<Path> jarList,
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

    static List<MatchItem> jar2MatchItemList(Path jarPath, TextMatch textMatch, boolean quick) {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
            // (1) entry
            Path rootPath = path.getRoot();
            Path relativizedPath = rootPath.relativize(path);
            String entry = relativizedPath.toString();

            // (2) logic
            LogicAssistant<TextMatch> logic = LogicAssistant.<TextMatch>builder()
                    .withClass(TextMatch.class)
                    .withLookup(TextMatch.lookup());
            TextMatch match = logic.and(textMatch, TextMatch.endsWith(".class"));

            // (3) test
            return match.test(entry);
        };
        return jar2MatchItemList(jarPath, predicate, quick);
    }

    static List<MatchItem> jar2MatchItemList(Path jarPath, BiPredicate<Path, BasicFileAttributes> predicate, boolean quick) {
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

    static List<MatchItem> jar2MatchItemList(Path jarPath, boolean quick, Function<byte[], List<MatchItem>> func) {
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
            BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> {
                String entry = path.toString();
                return TextMatch.endsWith(".class").test(entry);
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
