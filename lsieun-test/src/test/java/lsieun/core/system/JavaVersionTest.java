package lsieun.core.system;

import org.junit.jupiter.api.Test;

class JavaVersionTest {
    @Test
    void testCurrent() {
        JavaVersion javaVersion = JavaVersion.current();
        System.out.println(javaVersion);
    }
}
