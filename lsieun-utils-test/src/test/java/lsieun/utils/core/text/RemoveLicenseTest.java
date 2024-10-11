package lsieun.utils.core.text;

import lsieun.utils.core.io.dir.DirectoryUtils;
import lsieun.utils.core.io.file.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RemoveLicenseTest {
    String dirPath = "D:\\git-src\\netty-maven-simple";
    String fileExt = ".java";

    private static void process(String dirPath, String fileExt,
                                Function<List<String>, List<String>> func,
                                BiConsumer<String, List<String>> consumer) {
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
            List<String> lineList = FileUtils.readLines(filepath);

            List<String> resultList = func.apply(lineList);
            // FileUtils.writeLines(filepath, resultList);
            consumer.accept(filepath, resultList);
        }
    }

    @Test
    public void testRemove() {
        process(dirPath, fileExt,
                list -> {
                    String firstLine = list.get(0);
                    if (!firstLine.startsWith("/*")) {
                        return list;
                    }
                    int size = list.size();
                    int index = 0;
                    for (int i = 1; i < size; i++) {
                        String line = list.get(i);
                        if (line.trim().endsWith("*/")) {
                            index = i;
                            break;
                        }
                    }

                    List<String> newList = new ArrayList<>();
                    for (int i = index + 1; i < size; i++) {
                        String line = list.get(i);
                        newList.add(line);
                    }

                    return newList;
                },
                FileUtils::writeLines
        );
    }

    @Test
    public void testRemovePrecedingBlankLine() {
        process(dirPath, fileExt,
                list -> {
                    int size = list.size();
                    int index = 0;
                    for (int i = 0; i < size; i++) {
                        String line = list.get(i);
                        if (!line.trim().equals("")) {
                            index = i;
                            break;
                        }
                    }

                    List<String> newList = new ArrayList<>();
                    for (int i = index; i < size; i++) {
                        String line = list.get(i);
                        newList.add(line);
                    }

                    return newList;
                },
                FileUtils::writeLines
        );
    }
}
