package lsieun.base.ds.box;

public class Triplet<T1, T2, T3> {
    private final T1 first;
    private final T2 second;
    private final T3 third;

    private Triplet(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }

    public T3 third() {
        return third;
    }

    public static <T, S, R> Triplet<T, S, R> of(T first, S second, R third) {
        return new Triplet<>(first, second, third);
    }
}