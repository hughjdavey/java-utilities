package utilities;

import java.util.Collection;
import java.util.function.Predicate;

public class Predicates {

    public static <T> Predicate<T> and(final Predicate<T> one, final Predicate<T> two) {
        return one.and(two);
    }

    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return predicate.negate();
    }

    public static <T> Predicate<T> or(final Predicate<T> one, final Predicate<T> two) {
        return one.or(two);
    }

    public static <T> Predicate<T> xor(final Predicate<T> one, final Predicate<T> two) {
        return and(or(one, two), not(and(one, two)));
    }

    public static Predicate<Integer> even() {
        return i -> i % 2 == 0;
    }

    public static Predicate<Integer> odd() {
        return not(even());
    }

    public static Predicate<Collection<?>> empty() {
        return Collection::isEmpty;
    }
}
