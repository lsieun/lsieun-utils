package lsieun.utils.text;

import lsieun.utils.io.DirectoryUtils;
import lsieun.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class MarkDownTest {
    String dirPath = "D:\\git-repo\\lsieun.github.io\\_git";
    String fileExt = ".md";

    @Test
    public void testProcessSpace() {
        PanguSpacing.COMPACT_SPACE = false;
        process(dirPath, fileExt,
                lineList -> lineList.stream()
                        .map(PanguSpacing::process)
                        .collect(Collectors.toList()),
                FileUtils::writeLines
        );
    }

    @Test
    public void testNormalizeBlank() {
        process(dirPath, fileExt,
                MarkDown::normalizeBlank,
                FileUtils::writeLines
        );
    }

    @Test
    public void testProcessFolder() {
        String content = "[UP](/git.html)";

        process(dirPath, fileExt,
                list -> MarkDown.addText(list, content),
                FileUtils::writeLines
//                FileUtils::print
        );
    }

    @Test
    public void testRemoveLine() {
        process(dirPath, fileExt,
                list -> MarkDown.removeLineByPrefix(list, "[UP]"),
                FileUtils::writeLines
        );
    }



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
}