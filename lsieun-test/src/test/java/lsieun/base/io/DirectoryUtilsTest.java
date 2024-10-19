package lsieun.base.io;

import lsieun.base.io.dir.DirNioUtils;
import lsieun.base.io.file.FileName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static lsieun.base.io.dir.DirectoryUtils.*;

class DirectoryUtilsTest {
    @Test
    void testFindByExtension() {
//        String dirPath = "C:\\Users\\1\\AppData\\Roaming\\Autodesk\\AutoCAD 2014";
        String dirPath = "D:\\software\\Autodesk\\AutoCAD 2014\\Sample";
        String fileExt = ".dwg";

        List<File> fileList = findAllFileInDirectory(dirPath);
        if (fileList.isEmpty()) {
            return;
        }

        for (File f : fileList) {
            String filepath = f.getAbsolutePath();
            if (!filepath.endsWith(fileExt)) {
                continue;
            }

            System.out.println(filepath);
        }
    }

    @Test
    void copyAllJarToDir() {
        String dirPath = "D:\\software\\Nexus\\nexus-3.40.1-01-win64\\nexus-3.40.1-01";
        List<File> fileList = findAllFileInDirectory(dirPath);

        List<File> resultList = new ArrayList<>();
        for (File f : fileList) {
            String filename = f.getName();
            boolean flag = FileName.hasExtension(filename, "jar");
            if (flag) {
                String filepath = f.getAbsolutePath();
                System.out.println(filepath);
                resultList.add(f);
            }
        }

        copyFileList2Directory(resultList, "D:\\tmp\\lib");
    }

    @Test
    void printDuplicateNames() {
        printDuplicateFileByName("D:\\git-repo\\lsieun.github.io\\_java");
    }

    @Test
    void testFindFileListInDirectory() {
        Path dirPath = Paths.get(".");
        List<Path> pathList = DirNioUtils.findFileListInDirByExt(dirPath, ".java");
        for (Path path : pathList) {
            System.out.println(path);
        }
    }

    @Test
    void testAddFileNamePrefixInDirectory() throws IOException {
        Path dirPath = Paths.get("D:\\git-repo\\lsieun.github.io\\_java\\fx");
        DirNioUtils.addFileNamePrefixInDirectory(dirPath, ".md", "javafx-");
    }
}