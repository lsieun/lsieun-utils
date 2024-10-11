package lsieun.utils.core.text;

import lsieun.utils.core.io.dir.DirectoryUtils;
import lsieun.utils.core.io.file.FileUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarkDownTest {
    String dirPath = "D:\\git-repo\\lsieun.github.io\\_java\\crypto";
    String fileExt = ".md";

    String upLink = "[UP](/java-crypto.html)";

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

    @Order(1)
    @Test
    public void testRemoveUpLink() {
        process(dirPath, fileExt,
                list -> MarkDown.removeLineByPrefix(list, "[UP]"),
                FileUtils::writeLines
        );
    }

    @Order(2)
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

    @Order(3)
    @Test
    public void testNormalizeBlank() {
        process(dirPath, fileExt,
                MarkDown::normalizeBlank,
                FileUtils::writeLines
        );
    }

    @Order(4)
    @Test
    public void testAddUpLink() {
        process(dirPath, fileExt,
                list -> MarkDown.addText(list, upLink),
                FileUtils::writeLines
//                FileUtils::print
        );
    }
}