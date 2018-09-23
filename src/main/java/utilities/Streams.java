package utilities;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Streams {

    public static IntStream reverseRange(final int from, final int to) {
        return IntStream.range(from, to).map(i -> to - i + from - 1);
    }

    public static IntStream reverseRangeClosed(final int from, final int to) {
        return IntStream.rangeClosed(from, to).map(i -> to - i + from);
    }

    public static <T> Stream<T> fromOptionals(final Collection<Optional<T>> maybeTs) {
        return maybeTs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
