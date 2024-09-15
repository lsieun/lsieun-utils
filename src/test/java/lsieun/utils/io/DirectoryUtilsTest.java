package lsieun.utils.io;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static lsieun.utils.io.DirectoryUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectoryUtilsTest {
    @Test
    void testFindByExtension()
    {
//        String dirPath = "C:\\Users\\1\\AppData\\Roaming\\Autodesk\\AutoCAD 2014";
        String dirPath = "D:\\software\\Autodesk\\AutoCAD 2014\\Sample";
        String fileExt = ".dwg";

        List<File> fileList = DirectoryUtils.findAllFileInDirectory(dirPath);
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
}