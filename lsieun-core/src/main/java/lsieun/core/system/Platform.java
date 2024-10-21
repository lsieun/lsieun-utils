package lsieun.core.system;

import jakarta.annotation.Nonnull;

/**
 * 来自 IntelliJ IDEA 的 util-8.jar 中 com.intellij.execution.Platform
 */
public enum Platform {
    WINDOWS('\\', ';', "\r\n"),
    UNIX('/', ':', "\n");

    public final char fileSeparator;
    public final char pathSeparator;
    public final @Nonnull String lineSeparator;

    Platform(char fileSeparator, char pathSeparator, @Nonnull String lineSeparator) {
        this.fileSeparator = fileSeparator;
        this.pathSeparator = pathSeparator;
        this.lineSeparator = lineSeparator;
    }

    public static @Nonnull Platform current() {
        return SystemInfo.isWindows ? WINDOWS : UNIX;
    }
}