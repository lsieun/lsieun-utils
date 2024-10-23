package lsieun.core.system;

import org.jetbrains.annotations.NotNull;

/**
 * 来自 IntelliJ IDEA 的 util-8.jar 中 com.intellij.execution.Platform
 */
public enum Platform {
    WINDOWS('\\', ';', "\r\n"),
    UNIX('/', ':', "\n");

    public final char fileSeparator;
    public final char pathSeparator;
    public final @NotNull String lineSeparator;

    Platform(char fileSeparator, char pathSeparator, @NotNull String lineSeparator) {
        this.fileSeparator = fileSeparator;
        this.pathSeparator = pathSeparator;
        this.lineSeparator = lineSeparator;
    }

    public static @NotNull Platform current() {
        return SystemInfo.isWindows ? WINDOWS : UNIX;
    }
}