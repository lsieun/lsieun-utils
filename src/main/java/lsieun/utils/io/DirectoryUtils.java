package lsieun.utils.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryUtils {
    public static List<File> findAllFileInDirectory(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return Collections.emptyList();
        }

        List<File> dirList = new ArrayList<>();
        if (file.isDirectory()) {
            dirList.add(file);
        }

        List<File> fileList = new ArrayList<>();
        if (file.isFile()) {
            fileList.add(file);
        }

        for (int i = 0; i < dirList.size(); i++) {
            File dirFile = dirList.get(i);
            File[] files = dirFile.listFiles();
            if (files == null) {
                continue;
            }

            for (File f : files) {
                if (f.isDirectory()) {
                    dirList.add(f);
                } else if (f.isFile()) {
                    fileList.add(f);
                } else {
                    System.out.println("file: " + f.getAbsolutePath());
                }
            }
        }

        return fileList;
    }

    public static void copyFileList2Directory(List<File> fileList, String destDirectory) {
        File destFile = new File(destDirectory);
        if (destFile.isFile()) {
            return;
        }

        for (File f : fileList) {
            String name = f.getName();
            String srcPath = f.getAbsolutePath();
            String destPath = destDirectory + File.separator + name;
            FileUtils.copy(srcPath, destPath);
        }
    }

    public static void main(String[] args) {
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
}
