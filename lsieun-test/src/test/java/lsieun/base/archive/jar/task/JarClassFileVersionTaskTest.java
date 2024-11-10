package lsieun.base.archive.jar.task;

import lsieun.base.ds.box.pair.Pair;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.task.jar.JarClassFileVersionTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class JarClassFileVersionTaskTest {
    @Test
    void testReadAllClassFileVersion() throws IOException {
        Path path = Path.of("D:\\service\\nexus-3.70.1-02\\system\\com\\fasterxml\\jackson\\core\\jackson-core\\2.17.0\\jackson-core-2.17.0.jar");
        List<Integer> versionList = JarClassFileVersionTask.readAllClassFileVersion(path);
        versionList.forEach(System.out::println);
    }

    @Test
    void testReadAllClassFileVersionInDir() throws IOException {
        Path dirPath = Path.of("D:\\service\\nexus-3.70.1-02\\system");
        List<Path> jarPathList = DirNioUtils.findFileListInDirByExt(dirPath, ".jar");
        List<Pair<Path, Integer>> pairList = JarClassFileVersionTask.readAllClassFileVersionInDir(jarPathList);
        Pair.printCountMap(pairList, Pair::second, 0);
        Pair.printGroupMap(pairList, Pair::second, Pair::first);
    }
}