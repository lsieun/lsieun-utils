package lsieun.base.ds.box;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple value wrapper.
 * <p>
 * com.intellij.openapi.util.Ref
 *
 * @param <T> Value type.
 */
public class Ref<T> {
    private T myValue;

    public Ref() {
    }

    public Ref(@Nullable T value) {
        this.myValue = value;
    }

    public final boolean isNull() {
        return (this.myValue == null);
    }

    public final T get() {
        return this.myValue;
    }

    public final void set(T value) {
        this.myValue = value;
    }

    public final boolean setIfNull(@Nullable T value) {
        boolean result = myValue == null && value != null;
        if (result) {
            myValue = value;
        }
        return result;
    }

    @NotNull
    public static <T> Ref<T> create() {
        return new Ref<>();
    }

    public static <T> Ref<T> create(@Nullable T value) {
        return new Ref<>(value);
    }

    @Nullable
    public static <T> T deref(@Nullable Ref<T> ref) {
        return ref == null ? null : ref.get();
    }

    @Override
    public String toString() {
        return String.valueOf(myValue);
    }
}