package lsieun.core.match.text;

import org.junit.jupiter.api.Test;

class TextMatchBuddyTest {
    @Test
    void testFileExt() {
        String[] array = {
                "D:/abc/Xyz.jpg",
                "D:/abc/Xyz.png",
                "D:\\abc\\Xyz.jpg",
                "D:\\abc\\Xyz.JPG", // 大写后缀
        };

        for (String str : array) {
            boolean flag = TextMatchBuddy.byFileExtension("jpg", "png").test(str);
            String msg = String.format("%s - %s", str, flag);
            System.out.println(msg);
        }
    }
}
