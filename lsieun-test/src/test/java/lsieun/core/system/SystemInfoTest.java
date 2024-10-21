package lsieun.core.system;

import lsieun.box.BoxUtils;
import org.junit.jupiter.api.Test;

class SystemInfoTest {
    @Test
    void testSystemInfo() {
        String[][] matrix = {
                {"OS_NAME", SystemInfo.OS_NAME},
                {"OS_ARCH", SystemInfo.OS_ARCH},
                {"OS_VERSION", SystemInfo.OS_VERSION},
                {"JAVA_VERSION", SystemInfo.JAVA_VERSION},
                {"JAVA_RUNTIME_VERSION", SystemInfo.JAVA_RUNTIME_VERSION},
                {"JAVA_VENDOR", SystemInfo.JAVA_VENDOR},
                {"isAArch64", String.valueOf(SystemInfo.isAarch64)},
                {"isWindows", String.valueOf(SystemInfo.isWindows)},
                {"isUnix", String.valueOf(SystemInfo.isUnix)},
                {"isSolaris", String.valueOf(SystemInfo.isSolaris)},
                {"isFreeBSD", String.valueOf(SystemInfo.isFreeBSD)},
                {"isMac", String.valueOf(SystemInfo.isMac)},
                {"isLinux", String.valueOf(SystemInfo.isLinux)},
                {"isOracleJvm", String.valueOf(SystemInfo.isOracleJvm)},
                {"isIbmJvm", String.valueOf(SystemInfo.isIbmJvm)},
                {"isAzulJvm", String.valueOf(SystemInfo.isAzulJvm)},
                {"isJetBrainsJvm", String.valueOf(SystemInfo.isJetBrainsJvm)},
                {"isWin8OrNewer", String.valueOf(SystemInfo.isWin8OrNewer)},
                {"isWin10OrNewer", String.valueOf(SystemInfo.isWin10OrNewer)},
                {"isWin11OrNewer", String.valueOf(SystemInfo.isWin11OrNewer)},
                {"isFileSystemCaseSensitive", String.valueOf(SystemInfo.isFileSystemCaseSensitive)},

        };
        BoxUtils.printTable(matrix);
    }
}
