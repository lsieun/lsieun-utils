package lsieun.utils.id;

import org.junit.jupiter.api.Test;

import java.util.Formatter;

class GlobalIdGeneratorTest {

    @Test
    void testGenerate() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        for (int i = 0; i < 30; i++) {
            String id = GlobalIdGenerator.generate();
            fm.format("id = %s%n", id);
            Thread.sleep(200L);
        }
        System.out.println(sb);
    }
}