package lsieun.base.ds.pair;

public final class IntPair {
    public final int first;
    public final int second;

    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        return 31 * this.first + this.second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            IntPair that = (IntPair)o;
            return this.first == that.first && this.second == that.second;
        } else {
            return false;
        }
    }

    public String toString() {
        return "first=" + this.first + ", second=" + this.second;
    }
}