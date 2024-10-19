package lsieun.task.jar;

import lsieun.annotation.method.MethodParamExample;
import lsieun.annotation.mind.blueprint.Intention;
import lsieun.base.archive.zip.ZipFindNioUtilsForMultiple;
import lsieun.base.ds.pair.Pair;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.io.file.FileNioUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JarCopyTask {
    @Intention({
            "问题：IntelliJ IDEA 有许多 jar 包，有一些是第三方的 jar 包，有一些是 Jetbrains 提供的 jar包，两种类型的 jar 包无法区分",
            "思路：根据 jar 包里的路径前缀，找到所有相关的 jar 包，然后复制到指定文件夹"
    })
    public static void copyJarToDstDirByEntryPaths(Path srcPath, int maxDepth, Path dstPath,
                                                   @MethodParamExample({"com/intellij/", "com/jetbrains/"})
                                                   String... entryPaths) throws IOException {
        // check srcPath
        if (!Files.exists(srcPath)) return;
        if (!Files.isDirectory(srcPath)) return;

        // check array
        if (entryPaths == null || entryPaths.length == 0) return;

        // jar list
        List<Path> fileList = DirNioUtils.findFileListInDirByExt(srcPath, maxDepth, ".jar");
        if (fileList.isEmpty()) return;

        // path list
        List<Path> candidateList = ZipFindNioUtilsForMultiple.findFileList(fileList, Arrays.asList(entryPaths));
        if (candidateList.isEmpty()) return;

        // copy file
        FileNioUtils.copyFileList2Dir(candidateList, dstPath);
    }

    @Intention({
            "问题：使用 Nexus 的时候，有一些类找不到，因此无法编译",
            "思路：根据这些类名，找到所有相关的 jar 包，然后复制到指定文件夹"
    })
    public static void copyJarToDstDirByClassNames(Path srcPath, Path dstPath, String... classnames) throws IOException {
        copyJarToDstDirByClassNames(srcPath, Integer.MAX_VALUE, dstPath, classnames);
    }

    public static void copyJarToDstDirByClassNames(Path srcPath, int maxDepth, Path dstPath, String... classnames) throws IOException {
        // source dir
        if (!Files.exists(srcPath)) return;
        if (!Files.isDirectory(srcPath)) return;

        // class array
        if (classnames == null || classnames.length == 0) return;

        // jar list
        List<Path> fileList = DirNioUtils.findFileListInDirByExt(srcPath, maxDepth, ".jar");
        if (fileList.isEmpty()) return;

        // pair list
        List<Pair<String, Path>> pairList = ZipFindNioUtilsForMultiple.findPairListByClassNames(fileList, classnames);
        if (pairList.isEmpty()) return;

        // map
        Map<String, List<Path>> groupMap = Pair.groupToMap(pairList, Pair::first, Pair::second);
        Map<Path, Long> countMap = Pair.countToMap(pairList, Pair::second);

        // one to one
        List<Pair<String, Path>> one2OnePairList = one2oneList(groupMap, countMap);
        one2OnePairList.forEach(pair -> {
            System.out.println(pair.first() + " " + pair.second());
        });

        // path list
        List<Path> candidateList = one2OnePairList.stream().map(Pair::second).distinct().toList();

        // copy file list
        FileNioUtils.copyFileList2Dir(candidateList, dstPath);
    }

    private static List<Pair<String, Path>> one2oneList(Map<String, List<Path>> groupMap, Map<Path, Long> countMap) {
        List<Pair<String, Path>> pairList = new ArrayList<>();
        groupMap.forEach((entry, list) -> {
            Path path = getOnePath(list, countMap);
            Pair<String, Path> pair = new Pair<>(entry, path);
            pairList.add(pair);
        });
        return pairList;
    }

    private static Path getOnePath(List<Path> pathList, Map<Path, Long> countMap) {
        Path targetPath = pathList.get(0);
        for (int i = 1; i < pathList.size(); i++) {
            Path path = pathList.get(i);
            targetPath = getOnePath(targetPath, path, countMap);
        }
        return targetPath;
    }

    private static Path getOnePath(Path firstPath, Path secondPath, Map<Path, Long> countMap) {
        long count1 = countMap.get(firstPath);
        long count2 = countMap.get(secondPath);
        if (count1 > count2) return firstPath;
        if (count1 < count2) return secondPath;

        int nameCount1 = firstPath.getNameCount();
        int nameCount2 = secondPath.getNameCount();
        if (nameCount1 < nameCount2) return firstPath;
        if (nameCount1 > nameCount2) return secondPath;

        int length1 = firstPath.toString().length();
        int length2 = secondPath.toString().length();
        if (length1 < length2) return firstPath;
        if (length1 > length2) return secondPath;
        return firstPath;
    }
}