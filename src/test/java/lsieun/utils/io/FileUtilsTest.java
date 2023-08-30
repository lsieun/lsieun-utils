package lsieun.utils.io;


import org.junit.jupiter.api.Test;

public class FileUtilsTest {
    @Test
    public void testNegateCopy() {
        String name = "011";
        String from = "D:\\tmp\\" + name + ".iso";
        String to = "D:\\tmp\\" + name + ".mp4";

        if (!FileUtils.exists(from)) {
            return;
        }
        if (!FileUtils.exists(to)) {
            return;
        }

        FileUtils.negateCopy(from, to);
    }
}