package lsieun.utils.asm.common;

import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.find.ClassVisitorForFindInsnByInsnInvoke;
import lsieun.utils.asm.visitor.find.ClassVisitorForFindMethod;
import lsieun.utils.asm.visitor.find.ClassVisitorForFindMethodByInsnInvoke;
import lsieun.utils.core.archive.zip.ZipContentNioUtils;
import lsieun.utils.core.ds.pair.Pair;
import lsieun.utils.core.io.dir.DirNioUtils;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * <h2>Find Methods in Directory</h2>
 * <pre>
 * Path dirPath = Paths.get("...");
 * MethodInfoMatch methodMatch = MethodInfoMatch.byReturnType(
 *         AsmTypeMatch.byType("...")
 * );
 * List<Pair<Path, List<MatchItem>>> list = ClassFileFindUtils.findInDir(
 *         dirPath, 1,
 *         bytes -> ClassFileFindUtils.findMethod(bytes, methodMatch)
 * );
 * ClassFileFindUtils.print(list);
 * </pre>
 */
public class ClassFileFindUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassFileFindUtils.class);

    public static List<MatchItem> findMethod(byte[] bytes, MethodInfoMatch methodMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindMethod cv = new ClassVisitorForFindMethod(methodMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    public static List<MatchItem> findMethodByInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindMethodByInsnInvoke cv = new ClassVisitorForFindMethodByInsnInvoke(methodMatch, insnInvokeMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    public static List<MatchItem> findInsnByInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindInsnByInsnInvoke cv = new ClassVisitorForFindInsnByInsnInvoke(methodMatch, insnInvokeMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     */
    public static List<MatchItem> find(List<byte[]> byteArrayList, Function<byte[], List<MatchItem>> func) {
        List<MatchItem> resultList = new ArrayList<>();
        for (byte[] bytes : byteArrayList) {
            List<MatchItem> list = func.apply(bytes);
            resultList.addAll(list);
        }
        return resultList;
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     */
    public static Pair<Path, List<MatchItem>> findInJar(Path jarPath,
                                                        Function<byte[], List<MatchItem>> func) throws IOException {
        logger.debug("jarPath: {0}", jarPath);
        List<Pair<String, byte[]>> pairList = ZipContentNioUtils.readClassBytesList(jarPath);
        List<byte[]> byteArrayList = pairList.stream().map(Pair::second).toList();
        List<MatchItem> matchItemList = find(byteArrayList, func);
        return new Pair<>(jarPath, matchItemList);
    }

    /**
     * @see ClassFileFindUtils#findMethod(byte[], MethodInfoMatch)
     * @see ClassFileFindUtils#findMethodByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     * @see ClassFileFindUtils#findInsnByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
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
     * @see ClassFileFindUtils#findInsnByInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch)
     */
    public static List<Pair<Path, List<MatchItem>>> findInDir(Path dirPath, int maxDepth,
                                                              Function<byte[], List<MatchItem>> func) throws IOException {
        logger.debug("dirPath: {0}", dirPath);
        logger.debug("maxDepth: {0}", maxDepth);
        List<Path> jarPathList = DirNioUtils.findFileListInDirByExt(dirPath, maxDepth, ".jar");
        return findInJarList(jarPathList, func);
    }

    public static void print(List<Pair<Path, List<MatchItem>>> pairList) {
        for (Pair<Path, List<MatchItem>> pair : pairList) {
            print(pair);
        }
    }

    public static void print(Pair<Path, List<MatchItem>> pair) {
        List<MatchItem> list = pair.second();
        if (list.isEmpty()) {
            return;
        }
        System.out.println(pair.first());
        for (MatchItem item : list) {
            System.out.println("    " + item);
        }
    }
}
