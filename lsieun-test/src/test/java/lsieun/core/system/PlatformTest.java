package lsieun.core.system;

import org.junit.jupiter.api.Test;

class PlatformTest {
    @Test
    void testCurrentPlatform() {
        Platform platform = Platform.current();
        System.out.println(platform);
    }
}
