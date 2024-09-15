package lsieun.utils.io;

import java.io.File;
import java.util.*;

public class DirectoryUtils {


    public static List<File> findAllFileInDirectory(String dirPath) {
        return findAllFileInDirectory(dirPath, true);
    }

    public static List<File> findAllFileInDirectory(String dirPath, boolean recursive) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return Collections.emptyList();
        }

        if (file.isFile()) {
            return Collections.singletonList(file);
        }

        List<File> dirList = new ArrayList<>();
        if (file.isDirectory()) {
            dirList.add(file);
        }

        List<File> fileList = new ArrayList<>();
        if (file.isFile()) {
            fileList.add(file);
        }
        if (!recursive) {
            return fileList;
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
                }
                else if (f.isFile()) {
                    fileList.add(f);
                }
                else {
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

    public static void printDuplicateFileByName(String dirPath) {
        List<File> fileList = findAllFileInDirectory(dirPath, true);
        Map<String, List<File>> map = new HashMap<>();
        for (File file : fileList) {
            String absolutePath = file.getAbsolutePath().replaceAll("\\\\", "/");
            int index = absolutePath.lastIndexOf("/");
            String filename = absolutePath.substring(index + 1);
            List<File> list = map.get(filename);
            if (list == null) {
                list = new ArrayList<>();
                list.add(file);
                map.put(filename, list);
            }
            else {
                list.add(file);
            }
        }

        List<String> fileNameList = new ArrayList<>();
        Set<Map.Entry<String, List<File>>> entries = map.entrySet();
        for (Map.Entry<String, List<File>> entry : entries) {
            String key = entry.getKey();
            List<File> list = entry.getValue();
            if (list.size() > 1) {
                fileNameList.add(key);
            }
        }

        if (fileNameList.isEmpty()) {
            System.out.println("EMPTY");
            return;
        }
        for (String filename : fileNameList) {
            List<File> list = map.get(filename);
            System.out.println(filename);
            for (File file : list) {
                System.out.println("    " + file.getAbsolutePath());
            }
        }
    }
}
