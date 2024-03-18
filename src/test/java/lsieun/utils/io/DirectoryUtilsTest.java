package lsieun.utils.io;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

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
}