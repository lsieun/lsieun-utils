package lsieun.utils.core.text.escape;

import java.util.Objects;

final class Platform {
    static char[] charBufferFromThreadLocal() {
        return Objects.requireNonNull(DEST_TL.get());
    }

    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
        protected char[] initialValue() {
            return new char[1024];
        }
    };
}