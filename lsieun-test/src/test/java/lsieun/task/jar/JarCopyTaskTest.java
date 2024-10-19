package lsieun.task.jar;

import lsieun.base.log.LogLevel;
import lsieun.base.log.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

class JarCopyTaskTest {
    @Test
    void testNexusCopyJarToDstDirByClassNames() throws IOException {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Path srcPath = Path.of("D:\\service\\nexus-3.70.1-02");
        Path dstPath = Path.of("D:\\tmp\\nexus");
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

        JarCopyTask.copyJarToDstDirByClassNames(srcPath, dstPath, classArray);
    }

    @Test
    void testIntelliJCopyJarToDstDirByClassNames() throws IOException {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Path srcPath = Path.of("D:\\ideaIU-2024.2.1.win\\lib");
        Path dstPath = Path.of("D:\\tmp\\intellij");
        String[] entryPaths = {
                "com/intellij/",
                "com/jetbrains/",
        };

        JarCopyTask.copyJarToDstDirByEntryPaths(srcPath, 1, dstPath, entryPaths);
    }
}