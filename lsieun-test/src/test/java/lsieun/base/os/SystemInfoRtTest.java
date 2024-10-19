package lsieun.base.os;

import org.junit.jupiter.api.Test;

import static lsieun.base.os.SystemInfoRt.*;

class SystemInfoRtTest {
    @Test
    void test() {
        System.out.println(OS_NAME);
        System.out.println(OS_VERSION);
        System.out.println(isWindows);
        System.out.println(isMac);
        System.out.println(isLinux);
        System.out.println(isFreeBSD);
        System.out.println(isSolaris);
        System.out.println(isUnix);
        System.out.println(isXWindow);
        System.out.println(isFileSystemCaseSensitive);
    }
}
