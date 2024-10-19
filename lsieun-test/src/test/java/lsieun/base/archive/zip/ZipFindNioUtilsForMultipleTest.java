package lsieun.base.archive.zip;

import lsieun.base.ds.pair.Pair;
import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.log.LogLevel;
import lsieun.base.log.Logger;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ZipFindNioUtilsForMultipleTest {
    @Test
    void testNexus() {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Path dirPath = Path.of("D:\\service\\nexus-3.70.1-02");
        if (!Files.exists(dirPath)) return;
        if (!Files.isDirectory(dirPath)) return;
        List<Path> jarPathList = DirNioUtils.findFileListInDirByExt(dirPath, ".jar");
        if (jarPathList.isEmpty()) return;

        String[] classArray = {
                "javax.annotation.Nullable",
                "javax.servlet.ServletContext",
                "org.apache.karaf.features.Feature",
                "org.osgi.framework.Bundle",
                "org.sonatype.nexus.bootstrap.internal.DirectoryHelper",
                "org.slf4j.Logger",
                "com.sonatype.nexus.licensing.ext.NexusProfessionalFeature",
                "de.schlichtherle.license.LicenseContent",
                "javax.inject.Inject",
                "org.sonatype.licensing.CustomLicenseContent",
        };

        List<Pair<String, Path>> pairList = ZipFindNioUtilsForMultiple.findPairListByClassNames(jarPathList, classArray);
        Pair.printGroupMap(pairList, Pair::first, Pair::second);
        System.out.println("=====================================");

        Pair.printCountMap(pairList, Pair::second, 2);
    }

    @Test
    void testFindFileList() {
        Path dirPath = Path.of("D:\\ideaIU-2024.2.1.win\\lib");
        List<Path> fileList = DirNioUtils.findFileListInDirByExt(dirPath, 1, ".jar");
        List<Path> candidateList = ZipFindNioUtilsForMultiple.findFileList(fileList, List.of("com/intellij/", "com/jetbrains/"));
        for (Path jarPath : candidateList) {
            System.out.println(dirPath.relativize(jarPath));
        }
    }
}