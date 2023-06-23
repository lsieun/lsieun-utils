package lsieun.utils.text;

import junit.framework.TestCase;
import lsieun.utils.io.FileUtils;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class PanguSpacingTest extends TestCase {

    @Test
    public void testProcess() {
        String filepath = "D:\\git-repo\\lsieun.github.io\\_k8s\\concept\\cni.md";
        boolean isExist = FileUtils.exists(filepath);
        if (!isExist) {
            return;
        }

        List<String> lineList = FileUtils.readLines(filepath);

        List<String> resultList = lineList.stream()
                .map(PanguSpacing::process)
                .collect(Collectors.toList());

        resultList.forEach(System.out::println);
    }
}