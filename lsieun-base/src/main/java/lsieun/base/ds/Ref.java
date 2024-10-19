package lsieun.base.ds;

public class Ref<T> {
    private T myValue;

    public Ref() {
    }

    public Ref(T value) {
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

    public final boolean setIfNull(T value) {
        boolean result = (this.myValue == null && value != null);
        if (result)
            this.myValue = value;
        return result;
    }

    public static <T> Ref<T> create() {
        return new Ref<>();
    }

    public static <T> Ref<T> create(T value) {
        return new Ref<>(value);
    }


    public static <T> T deref(Ref<T> ref) {
        return (ref == null) ? null : ref.get();
    }

    public String toString() {
        return String.valueOf(this.myValue);
    }
}