package lsieun.core.system;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * 来自 IntelliJ IDEA 的 util-8.jar 中 com.intellij.util.system.OS
 */
public enum OS {
    Windows,
    macOS,
    Linux,
    FreeBSD,
    Other;

    public static final OS CURRENT = fromString(System.getProperty("os.name"));

    OS() {
    }

    public @NotNull Platform getPlatform() {
        return this == Windows ? Platform.WINDOWS : Platform.UNIX;
    }

    public static @NotNull OS fromString(@Nullable String os) {
        if (os != null) {
            os = os.toLowerCase(Locale.ENGLISH);
            if (os.startsWith("windows")) {
                return Windows;
            }

            if (os.startsWith("mac")) {
                return macOS;
            }

            if (os.startsWith("linux")) {
                return Linux;
            }

            if (os.startsWith("freebsd")) {
                return FreeBSD;
            }
        }

        return Other;
    }
}