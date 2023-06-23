package lsieun.utils.io;

import junit.framework.TestCase;
import org.junit.Test;

public class FileUtilsTest extends TestCase {
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