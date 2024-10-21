package lsieun.asm.common.analysis;

import java.io.IOException;
import java.nio.file.Path;

public class JavapUtils {
    public static void print(String classname) {
        String line = String.format("javap -v -p %1$s > %1$s.txt", classname);
        System.out.println(line);
    }

    public static void print(String jarName, String classname) {
        String line = String.format("javap -cp %2$s -v -p %1$s > %1$s.txt", classname, jarName);
        System.out.println(line);
    }

    public static void dump(Path filepath, String classname) {
        try {
            String cmd = String.format("javap -cp %2$s -v -p %1$s > %1$s.txt", classname, filepath.toAbsolutePath().normalize());
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            System.out.println(exitCode);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Path filepath = Path.of("D:\\tmp\\lib\\product.jar");
        dump(filepath, "com.jetbrains.ls.responses.License");
    }
}
