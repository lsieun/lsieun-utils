package lsieun.base.io;


import lsieun.base.io.dir.DirectoryUtils;
import lsieun.base.io.file.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static lsieun.base.io.file.FileUtils.compareFilesByByte;

public class FileUtilsTest {
    @Test
    public void testNegateCopy() {
        String name = "011";
        String from = "D:\\tmp\\" + name + ".iso";
        String to = "D:\\tmp\\" + name + ".mp4";

        if (!FileUtils.exists(from)) {
            return;
        }
        if (!FileUtils.exists(to)) {
            return;
        }

        FileUtils.negateCopy(from, to);
    }

    @Test
    void findDuplicateFiles() throws IOException {
        List<File> fileList = DirectoryUtils.findAllFileInDirectory("D:/tmp");
        for (File file : fileList) {
            System.out.println(file);
        }
        int size = fileList.size();
        for (int i = 0; i < size - 1; i++) {
            File f1 = fileList.get(i);
            Path path1 = f1.toPath();
            for (int j = i + 1; j < size; j++) {
                File f2 = fileList.get(j);
                Path path2 = f2.toPath();
                long pos = compareFilesByByte(path1, path2);
                if (pos != -1) {
                    continue;
                }

                System.out.println("pos = " + pos);
                System.out.println(path1.toAbsolutePath());
                System.out.println(path2.toAbsolutePath());
                System.out.println("====================");
            }
        }
    }
}