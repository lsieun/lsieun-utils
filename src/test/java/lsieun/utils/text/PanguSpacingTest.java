package lsieun.utils.text;

import lsieun.utils.io.DirectoryUtils;
import lsieun.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class PanguSpacingTest {

    @Test
    public void testProcessSingleFile() {
        String filepath = "D:\\workspace\\git-repo\\lsieun.github.io\\_bytebuddy\\annotation\\annotation-04-add.md";
        boolean isExist = FileUtils.exists(filepath);
        if (!isExist) {
            return;
        }

        List<String> lineList = FileUtils.readLines(filepath);

        PanguSpacing.COMPACT_SPACE = false;
        List<String> resultList = lineList.stream()
                .map(PanguSpacing::process)
                .collect(Collectors.toList());

        resultList.forEach(System.out::println);
    }

    @Test
    public void testProcessFolder() {
        String dirPath = "D:\\workspace\\git-repo\\lsieun.github.io\\_bytebuddy";
        String fileExt = ".md";

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

            PanguSpacing.COMPACT_SPACE = false;
            List<String> resultList = lineList.stream()
                    .map(PanguSpacing::process)
                    .collect(Collectors.toList());
            FileUtils.writeLines(filepath, resultList);
        }
    }
}