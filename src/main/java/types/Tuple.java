package types;

import java.util.Objects;

public class Tuple<A, B> {

    final private A one;
    final private B two;

    public Tuple(final A one, final B two) {
        this.one = one;
        this.two = two;
    }

    public A getOne() {
        return one;
    }

    public B getTwo() {
        return two;
    }

    // getters for the Scala set ;)
    public A _1() {
        return one;
    }

    public B _2() {
        return two;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "one=" + one +
                ", two=" + two +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(one, tuple.one) &&
                Objects.equals(two, tuple.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }
}
