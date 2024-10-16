package lsieun.utils.core.text.escape;

import java.util.function.Function;

public abstract class Escaper {
    private final Function<String, String> asFunction = this::escape;

    public abstract String escape(String paramString);

    public final Function<String, String> asFunction() {
        return this.asFunction;
    }
}