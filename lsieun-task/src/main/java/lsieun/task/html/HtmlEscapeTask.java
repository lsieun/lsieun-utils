package lsieun.task.html;

import lsieun.base.io.file.FileContentUtils;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.base.text.escape.html.HtmlEscapers;

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
