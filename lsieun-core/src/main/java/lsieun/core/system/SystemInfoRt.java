package lsieun.core.system;

import java.util.Locale;

public final class SystemInfoRt {
    public static final String OS_NAME;

    public static final String OS_VERSION;

    static {
        String name = System.getProperty("os.name");
        String version = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);
        if (name.startsWith("Windows") && name.matches("Windows \\d+")) {
            try {
                String version2 = name.substring("Windows".length() + 1) + ".0";
                if (Float.parseFloat(version2) > Float.parseFloat(version))
                    version = version2;
            } catch (NumberFormatException ignored) {
            }
            name = "Windows";
        }
        OS_NAME = name;
        OS_VERSION = version;
    }

    private static final String _OS_NAME = OS_NAME.toLowerCase(Locale.ENGLISH);

    public static final boolean isWindows = _OS_NAME.startsWith("windows");

    public static final boolean isMac = _OS_NAME.startsWith("mac");

    public static final boolean isLinux = _OS_NAME.startsWith("linux");

    public static final boolean isFreeBSD = _OS_NAME.startsWith("freebsd");

    public static final boolean isSolaris = _OS_NAME.startsWith("sunos");

    public static final boolean isUnix = !isWindows;

    public static final boolean isXWindow = (isUnix && !isMac);

    public static final boolean isFileSystemCaseSensitive = (isUnix && !isMac);
}