package lsieun.core.system;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.File;

public final class SystemInfo {
    public static final String OS_NAME;
    public static final String OS_VERSION;
    public static final String OS_ARCH;
    public static final String JAVA_VERSION;
    public static final String JAVA_RUNTIME_VERSION;
    public static final String JAVA_VENDOR;
    public static final boolean isAarch64;
    public static final boolean isWindows;
    public static final boolean isMac;
    public static final boolean isLinux;
    public static final boolean isFreeBSD;
    public static final boolean isSolaris;
    public static final boolean isUnix;
    public static final boolean isChromeOS;
    public static final boolean isOracleJvm;
    public static final boolean isIbmJvm;
    public static final boolean isAzulJvm;
    public static final boolean isJetBrainsJvm;
    public static final boolean isWin8OrNewer;
    public static final boolean isWin10OrNewer;
    public static final boolean isWin11OrNewer;
    public static final boolean isWayland;
    public static final boolean isXWindow;
    public static final boolean isGNOME;
    public static final boolean isKDE;
    public static final boolean isXfce;
    public static final boolean isI3;
    public static final boolean isFileSystemCaseSensitive;

    public static final boolean isMacOSCatalina;
    public static final boolean isMacOSBigSur;
    public static final boolean isMacOSMonterey;
    public static final boolean isMacOSVentura;
    public static final boolean isMacOSSonoma;


    public SystemInfo() {
    }

    private static String getRtVersion(String fallback) {
        String rtVersion = System.getProperty("java.runtime.version");
        return rtVersion != null && Character.isDigit(rtVersion.charAt(0)) ? rtVersion : fallback;
    }

    private static boolean isCrostini() {
        return (new File("/dev/.cros_milestone")).exists();
    }

    public static boolean isOsVersionAtLeast(@Nonnull String version) {
        return compareVersionNumbers(OS_VERSION, version) >= 0;
    }

    private static int compareVersionNumbers(@Nullable String v1, @Nullable String v2) {
        if (v1 == null && v2 == null) {
            return 0;
        } else if (v1 == null) {
            return -1;
        } else if (v2 == null) {
            return 1;
        } else {
            String[] part1 = v1.split("[._\\-]");
            String[] part2 = v2.split("[._\\-]");

            int idx;
            for(idx = 0; idx < part1.length && idx < part2.length; ++idx) {
                String p1 = part1[idx];
                String p2 = part2[idx];
                int cmp;
                if (p1.matches("\\d+") && p2.matches("\\d+")) {
                    cmp = Integer.valueOf(p1).compareTo(Integer.valueOf(p2));
                } else {
                    cmp = part1[idx].compareTo(part2[idx]);
                }

                if (cmp != 0) {
                    return cmp;
                }
            }

            if (part1.length != part2.length) {
                boolean left = part1.length > idx;

                for(String[] parts = left ? part1 : part2; idx < parts.length; ++idx) {
                    String p = parts[idx];
                    int cmp;
                    if (p.matches("\\d+")) {
                        cmp = Integer.valueOf(p).compareTo(0);
                    } else {
                        cmp = 1;
                    }

                    if (cmp != 0) {
                        return left ? cmp : -cmp;
                    }
                }
            }

            return 0;
        }
    }


    public static String getOsName() {
        return isMac ? "macOS" : OS_NAME;
    }

    public static String getOsNameAndVersion() {
        return getOsName() + ' ' + OS_VERSION;
    }

    private static int normalize(int number) {
        return Math.min(number, 9);
    }

    private static int toInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException var2) {
            return 0;
        }
    }

    static {
        OS_NAME = SystemInfoRt.OS_NAME;
        OS_VERSION = SystemInfoRt.OS_VERSION;
        OS_ARCH = System.getProperty("os.arch");
        JAVA_VERSION = System.getProperty("java.version");
        JAVA_RUNTIME_VERSION = getRtVersion(JAVA_VERSION);
        JAVA_VENDOR = System.getProperty("java.vm.vendor", "Unknown");
        isAarch64 = OS_ARCH.equals("aarch64");
        isWindows = SystemInfoRt.isWindows;
        isMac = SystemInfoRt.isMac;
        isLinux = SystemInfoRt.isLinux;
        isFreeBSD = SystemInfoRt.isFreeBSD;
        isSolaris = SystemInfoRt.isSolaris;
        isUnix = SystemInfoRt.isUnix;
        isChromeOS = isLinux && isCrostini();
        isOracleJvm = indexOfIgnoreCase(JAVA_VENDOR, "Oracle", 0) >= 0;
        isIbmJvm = indexOfIgnoreCase(JAVA_VENDOR, "IBM", 0) >= 0;
        isAzulJvm = indexOfIgnoreCase(JAVA_VENDOR, "Azul", 0) >= 0;
        isJetBrainsJvm = indexOfIgnoreCase(JAVA_VENDOR, "JetBrains", 0) >= 0;
        isWin8OrNewer = isWindows && isOsVersionAtLeast("6.2");
        isWin10OrNewer = isWindows && isOsVersionAtLeast("10.0");
        isWin11OrNewer = isWindows && isOsVersionAtLeast("11.0");
        isXWindow = SystemInfoRt.isUnix && !SystemInfoRt.isMac;
        if (SystemInfoRt.isUnix && !SystemInfoRt.isMac) {
            isWayland = System.getenv("WAYLAND_DISPLAY") != null;
            String desktop = System.getenv("XDG_CURRENT_DESKTOP");
            String gdmSession = System.getenv("GDMSESSION");
            isGNOME = desktop != null && desktop.contains("GNOME") || gdmSession != null && gdmSession.contains("gnome");
            isKDE = !isGNOME && (desktop != null && desktop.contains("KDE") || System.getenv("KDE_FULL_SESSION") != null);
            isXfce = !isGNOME && !isKDE && desktop != null && desktop.contains("XFCE");
            isI3 = !isGNOME && !isKDE && !isXfce && desktop != null && desktop.contains("i3");
        } else {
            isI3 = false;
            isXfce = false;
            isKDE = false;
            isGNOME = false;
            isWayland = false;
        }

        isFileSystemCaseSensitive = SystemInfoRt.isFileSystemCaseSensitive;
        isMacOSCatalina = isMac && isOsVersionAtLeast("10.15");
        isMacOSBigSur = isMac && isOsVersionAtLeast("10.16");
        isMacOSMonterey = isMac && isOsVersionAtLeast("12.0");
        isMacOSVentura = isMac && isOsVersionAtLeast("13.0");
        isMacOSSonoma = isMac && isOsVersionAtLeast("14.0");
    }

    public static int indexOfIgnoreCase(@Nonnull String where, @Nonnull String what, int fromIndex) {
        return indexOfIgnoreCase((CharSequence)where, (CharSequence)what, fromIndex);
    }


    public static int indexOfIgnoreCase(@Nonnull CharSequence where, @Nonnull CharSequence what, int fromIndex) {

        int targetCount = what.length();
        int sourceCount = where.length();
        if (fromIndex >= sourceCount) {
            return targetCount == 0 ? sourceCount : -1;
        } else {
            if (fromIndex < 0) {
                fromIndex = 0;
            }

            if (targetCount == 0) {
                return fromIndex;
            } else {
                char first = what.charAt(0);
                int max = sourceCount - targetCount;

                for(int i = fromIndex; i <= max; ++i) {
                    if (!charsEqualIgnoreCase(where.charAt(i), first)) {
                        do {
                            ++i;
                        } while(i <= max && !charsEqualIgnoreCase(where.charAt(i), first));
                    }

                    if (i <= max) {
                        int j = i + 1;
                        int end = j + targetCount - 1;

                        for(int k = 1; j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k)); ++k) {
                            ++j;
                        }

                        if (j == end) {
                            return i;
                        }
                    }
                }

                return -1;
            }
        }
    }


    public static int indexOfIgnoreCase(@Nonnull String where, char what, int fromIndex) {

        int sourceCount = where.length();

        for(int i = Math.max(fromIndex, 0); i < sourceCount; ++i) {
            if (charsEqualIgnoreCase(where.charAt(i), what)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean charsEqualIgnoreCase(char a, char b) {
        return charsMatch(a, b, true);
    }

    public static boolean charsMatch(char c1, char c2, boolean ignoreCase) {
        return compare(c1, c2, ignoreCase) == 0;
    }

    public static int compare(char c1, char c2, boolean ignoreCase) {
        int d = c1 - c2;
        if (d != 0 && ignoreCase) {
            char u1 = toUpperCase(c1);
            char u2 = toUpperCase(c2);
            d = u1 - u2;
            if (d != 0) {
                d = toLowerCase(u1) - toLowerCase(u2);
            }

            return d;
        } else {
            return d;
        }
    }

    public static char toUpperCase(char a) {
        if (a < 'a')
            return a;
        if (a <= 'z')
            return (char)(a + -32);
        return Character.toUpperCase(a);
    }

    public static char toLowerCase(char a) {
        if (a <= 'z')
            return (a >= 'A' && a <= 'Z') ? (char)(a + 32) : a;
        return Character.toLowerCase(a);
    }
}