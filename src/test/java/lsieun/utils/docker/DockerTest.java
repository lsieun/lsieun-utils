package lsieun.utils.docker;


import lsieun.utils.io.FileUtils;
import org.junit.Test;

import java.util.Formatter;
import java.util.List;

public class DockerTest {
    @Test
    public void testReadFile() {
        String filepath = "D:\\workspace\\git-repo\\lsieun-utils\\src\\test\\resources\\docker-image.txt";
        List<String> lines = FileUtils.readLines(filepath);

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
