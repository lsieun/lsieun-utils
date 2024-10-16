package lsieun.utils.task.html;

import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.core.text.escape.html.HtmlEscapers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class HtmlEscapeTask {
    public static void main(String[] args) throws IOException {
        Path path = ResourceUtils.readFilePath("html-input.txt");
        List<String> lines = FileContentUtils.readLines(path);
        lines.stream()
                .map(str -> HtmlEscapers.htmlEscaper().escape(str))
                .forEach(System.out::println);
    }
}
