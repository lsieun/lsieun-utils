package lsieun.asm.common.analysis;

public class JavapUtils {
    public static void print(String classname) {
        String line = String.format("javap -v -p %1$s > %1$s.txt", classname);
        System.out.println(line);
    }

    public static void print(String jarName, String classname) {
        String line = String.format("javap -cp %2$s -v -p %1$s > %1$s.txt", classname, jarName);
        System.out.println(line);
    }
}
