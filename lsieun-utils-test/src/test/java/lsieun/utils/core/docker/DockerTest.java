package lsieun.utils.core.docker;


import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.List;

public class DockerTest {
    @Test
    public void testReadFile() throws IOException {
        Path path = ResourceUtils.readFilePath("docker-image.txt");
        List<String> lines = FileContentUtils.readLines(path);

        String privateRepo = "docker.lan.net:8083/";

        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);

        for (String line : lines) {
            int dotIndex = line.indexOf(".");
            int slashIndex = line.indexOf("/");

            String image = line;
            if (dotIndex < slashIndex) {
                image = line.substring(slashIndex + 1);
            }

            String privateImage = privateRepo + image;


            fm.format("docker pull \"%s\"%n", privateImage);
            fm.format("docker tag \"%s\" \"%s\"%n", privateImage, line);
            fm.format("docker rmi \"%s\"%n", privateImage);

        }

        System.out.println(sb);
    }

    @Test
    public void testChange() {
        String name = "ubuntu:jammy-20230605";
        String firstName = String.format("docker.lan.net:8083/%s", name);
        String secondName = String.format("registry.lsieun.cn/library/%s", name);

        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("docker pull %s%n", firstName);
        fm.format("docker tag %s %s%n", firstName, secondName);
        fm.format("docker push %s%n", secondName);

        System.out.println(sb);
    }
}
