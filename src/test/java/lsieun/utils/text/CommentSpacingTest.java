package lsieun.utils.text;

import lsieun.utils.io.DirectoryUtils;
import lsieun.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

class CommentSpacingTest {
    @Test
    public void testProcessFolder() {
        String dirPath = "D:\\workspace\\git-repo\\DotNetARX\\TextStyleTools.cs";
        String fileExt = ".cs";

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

            List<String> resultList = lineList.stream()
                    .map(CommentSpacing::process)
                    .collect(Collectors.toList());
            FileUtils.writeLines(filepath, resultList);
        }
    }
}