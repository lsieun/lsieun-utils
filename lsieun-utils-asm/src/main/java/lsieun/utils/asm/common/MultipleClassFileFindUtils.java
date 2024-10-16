package lsieun.utils.asm.common;

import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.archive.zip.ZipContentNioUtils;
import lsieun.utils.core.ds.pair.Pair;
import lsieun.utils.core.io.dir.DirNioUtils;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import lsieun.utils.core.text.StrConst;
import lsieun.utils.match.text.TextMatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * <code>
 * <pre>
 * Path dirPath = Paths.get(&quot;...&quot;);
 * MethodInfoMatch methodMatch = MethodInfoMatch.byReturnType(
 *         AsmTypeMatch.byType(&quot;...&quot;)
 * );
 *
 * List&lt;Pair&lt;Path, List&lt;MatchItem&gt;&gt;&gt; list = MultipleClassFileFindUtils.findInDir(
 *         dirPath, 1,
 *         bytes -&gt; ClassFileFindUtils.findMethod(bytes, methodMatch)
 * );
 * MultipleClassFileFindUtils.print(list);
 * </pre>
 * </code>
 */
public class MultipleClassFileFindUtils {
    private static final Logger logger = LoggerFactory.getLogger(MultipleClassFileFindUtils.class);

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInvokeXxx(byte[], MethodInfoMatch, InsnInvokeMatch, boolean)
     */
    public static List<MatchItem> findByByteArray(List<byte[]> byteArrayList, Function<byte[], List<MatchItem>> func) {
        List<MatchItem> resultList = new ArrayList<>();
        for (byte[] bytes : byteArrayList) {
            List<MatchItem> list = func.apply(bytes);
            resultList.addAll(list);
        }
        return resultList;
    }

    public static List<MatchItem> findByEntryName(List<String> entryList, TextMatch textMatch) {
        return null;
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInvokeXxx(byte[], MethodInfoMatch, InsnInvokeMatch, boolean)
     */
    public static Pair<Path, List<MatchItem>> findInJar(Path jarPath,
                                                        Function<byte[], List<MatchItem>> func) throws IOException {
        logger.debug("jarPath: {0}", jarPath);
        List<Pair<String, byte[]>> pairList = ZipContentNioUtils.readClassBytesList(jarPath);
        List<byte[]> byteArrayList = pairList.stream().map(Pair::second).toList();
        List<MatchItem> matchItemList = findByByteArray(byteArrayList, func);
        return new Pair<>(jarPath, matchItemList);
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInvokeXxx(byte[], MethodInfoMatch, InsnInvokeMatch, boolean)
     */
    public static List<Pair<Path, List<MatchItem>>> findInJarList(List<Path> jarPathList,
                                                                  Function<byte[], List<MatchItem>> func) throws IOException {
        List<Pair<Path, List<MatchItem>>> resultList = new ArrayList<>();
        for (Path path : jarPathList) {
            var pair = findInJar(path, func);
            resultList.add(pair);
        }
        return resultList;
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInvokeXxx(byte[], MethodInfoMatch, InsnInvokeMatch, boolean)
     */
    public static List<Pair<Path, List<MatchItem>>> findInDir(Path dirPath, int maxDepth,
                                                              Function<byte[], List<MatchItem>> func) throws IOException {
        logger.debug("dirPath: {0}", dirPath);
        logger.debug("maxDepth: {0}", maxDepth);
        List<Path> jarPathList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".jar");
        return findInJarList(jarPathList, func);
    }

    public static void print(List<Pair<Path, List<MatchItem>>> pairList) {
        if (pairList == null || pairList.isEmpty()) {
            System.out.println("EMPTY");
            return;
        }

        int size = pairList.size();
        for (int i = 0; i < size; i++) {
            int index = i + 1;
            Pair<Path, List<MatchItem>> pair = pairList.get(i);
            Path path = pair.first();
            List<MatchItem> list = pair.second();
            String line = list.isEmpty() ?
                    String.format("|%03d| %s", index, path) :
                    String.format("|%03d| %s (%d)", index, path, list.size());
            System.out.println(line);

            print(list, 4);
        }
    }

    public static void print(Pair<Path, List<MatchItem>> pair) {
        List<MatchItem> list = pair.second();
        if (list.isEmpty()) {
            return;
        }
        System.out.println(pair.first());
        print(list, 4);
    }

    public static void print(List<MatchItem> matchItemList, int indent) {
        if (matchItemList.isEmpty()) {
            return;
        }

        int size = matchItemList.size();
        for (int i = 0; i < size; i++) {
            int index = i + 1;
            MatchItem item = matchItemList.get(i);
            String line = String.format("%s(%03d) %s", StrConst.SPACE.repeat(indent), index, item);
            System.out.println(line);

            List<MatchItem> children = item.children;
            if (!children.isEmpty()) {
                print(children, indent + 4);
            }
        }
    }
}
