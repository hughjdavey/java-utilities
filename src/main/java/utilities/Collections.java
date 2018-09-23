package utilities;

import types.Pair;
import types.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utilities.Predicates.empty;
import static utilities.Predicates.not;
import static utilities.Predicates.odd;
import static utilities.Streams.reverseRange;

public class Collections {

    /** CREATORS **/

    public static <T> Collection<T> newCollection() {
        return new ArrayList<>();
    }

    public static <T> Collection<T> newCollection(final T... ts) {
        return Arrays.stream(ts).collect(Collectors.toList());
    }

    public static <T> Collection<T> fill(final T t, final int times) {
        return IntStream.range(0, times).mapToObj(i -> t).collect(Collectors.toList());
    }

    /** VALIDATORS **/

    public static <T> boolean isNullOrEmpty(final Collection<T> ts) {
        return Objects.isNull(ts) || ts.isEmpty();
    }

    public static <T> boolean notNullOrEmpty(final Collection<T> ts) {
        return !isNullOrEmpty(ts);
    }

    /** GETTERS **/

    public static <T> T get(final Collection<T> ts, final int index) {
        return new ArrayList<>(ts).get(index);
    }

    public static <T> Collection<T> slice(final Collection<T> ts, final int fromIndex, final int toIndex) {
        return new ArrayList<>(ts).subList(fromIndex, toIndex);
    }

    public static <T> Optional<T> find(final Collection<T> ts, final Predicate<T> predicate) {
        for (final T t : ts) {
            if (predicate.test(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public static <T> OptionalInt findIndex(final Collection<T> ts, final Predicate<T> predicate) {
        int index = 0;
        for (final T t : ts) {
            if (predicate.test(t)) {
                return OptionalInt.of(index);
            }
            index++;
        }
        return OptionalInt.empty();
    }


    /* * * * * * * * * * * * *
     * [1, 2, 3, 4, 5]       *
     *  ^               head *
     *     ^--------^   tail *
     *  ^--------^      init *
     *              ^   last *
     *     ^-----^      mid  *
     * * * * * * * * * * * * */

    public static <T> T first(final Collection<T> ts) {
        return head(ts);
    }

    public static <T> T last(final Collection<T> ts) {
        return get(ts, ts.size() - 1);
    }

    public static <T> T head(final Collection<T> ts) {
        return get(ts, 0);
    }

    public static <T> Collection<T> init(final Collection<T> ts) {
        return slice(ts, 0, ts.size() - 1);
    }

    public static <T> Collection<T> tail(final Collection<T> ts) {
        return slice(ts, 1, ts.size());
    }

    public static <T> Collection<T> mid(final Collection<T> ts) {
        return slice(ts, 1, ts.size() - 1);
    }

    /** TRANSFORMERS **/

    public static <T> Collection<T> flatten(final Collection<T>... cs) {
        return Arrays.stream(cs).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> Collection<Pair<T>> toPairs(final Collection<T> ts) {
        if (odd().test(ts.size())) {
            throw new IllegalArgumentException("Odd number of elements");
        }

        return IntStream.iterate(2, i -> i + 2).limit(ts.size() / 2)
                .mapToObj(i -> new Pair<>(get(ts, i - 2), get(ts, i - 1)))
                .collect(Collectors.toList());
    }

    public static <T> Collection<Pair<T>> toOverlappingPairs(final Collection<T> ts) {
        return IntStream.range(1, ts.size())
                .mapToObj(i -> new Pair<>(get(ts, i - 1), get(ts, i)))
                .collect(Collectors.toList());
    }

    public static <T> Collection<Tuple<Integer, T>> indexed(final Collection<T> ts) {
        return indexedFrom(ts, 0);
    }

    public static <T> Collection<Tuple<Integer, T>> indexedFrom(final Collection<T> ts, final int startIndex) {
        return IntStream.range(0, ts.size())
                .mapToObj(i -> new Tuple<>(i + startIndex, get(ts, i)))
                .collect(Collectors.toList());
    }

    public static <A, B> Collection<Tuple<A, B>> zip(final Collection<A> as, final Collection<B> bs) {
        if (as.size() != bs.size()) {
            throw new IllegalArgumentException("Cannot zip two collections of different sizes");
        }

        return indexed(bs).stream()
                .map(tuple -> new Tuple<>(get(as, tuple._1()), tuple._2()))
                .collect(Collectors.toList());
    }

    public static <A, B> Map<A, B> zipToMap(final Collection<A> as, final Collection<B> bs) {
        return zip(as, bs).stream().collect(Collectors.toMap(Tuple::_1, Tuple::_2));
    }

    public static <A, B> Tuple<Collection<A>, Collection<B>> unzip(final Collection<Tuple<A, B>> zipped) {
        final Tuple<Collection<A>, Collection<B>> unzipped = new Tuple<>(newCollection(), newCollection());
        zipped.forEach(tuple -> {
            unzipped._1().add(tuple._1());
            unzipped._2().add(tuple._2());
        });
        return unzipped;
    }

    public static <T> Collection<Collection<T>> partition(final Collection<T> ts, final int partitionSize) {
        validatePartitionSize(partitionSize, 1);

        return IntStream.iterate(0, i -> i + partitionSize).limit((ts.size() / partitionSize) + 1)
                .mapToObj(i -> slice(ts, i, i + partitionSize > ts.size() ? ts.size() : i + partitionSize))
                .filter(not(empty()))
                .collect(Collectors.toList());
    }

    public static <T> Collection<Collection<T>> discardingPartition(final Collection<T> ts, final int partitionSize) {
        validatePartitionSize(partitionSize, 1);

        return IntStream.iterate(0, i -> i + partitionSize).limit(ts.size() / partitionSize)
                .mapToObj(i -> slice(ts, i, i + partitionSize))
                .collect(Collectors.toList());
    }

    /**
     * Partition a collection similarly to {@code partition}, but guarantees no partition will have a size of 1
     */
    public static <T> Collection<Collection<T>> partitionNoSingletons(final Collection<T> ts, final int partitionSize) {
        validatePartitionSize(partitionSize, 2);

        if (ts.size() % partitionSize == 1) {
            final List<T> mutableTs = new ArrayList<>(ts);
            final Collection<T> last2Ts = newCollection(mutableTs.remove(mutableTs.size() - 2), mutableTs.remove(mutableTs.size() - 1));

            final Collection<Collection<T>> partitioned = partition(mutableTs, partitionSize);
            partitioned.add(last2Ts);
            return partitioned;
        }
        return partition(ts, partitionSize);
    }


    public static <T> Collection<T> reverse(final Collection<T> ts) {
        return reverseRange(0, ts.size())
                .mapToObj(i -> get(ts, i))
                .collect(Collectors.toList());
    }

    public static <T> Collection<Integer> indicesOf(final Collection<T> ts, final T t) {
        return indexed(ts).stream()
                .filter(tuple -> Objects.equals(t, tuple._2()))
                .map(Tuple::_1)
                .collect(Collectors.toList());
    }

    public static <T> Collection<T> applyPredicate(final Collection<T> ts, final Predicate<T> predicate) {
        return ts.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static void validatePartitionSize(int partitionSize, final int minimumSize) {
        if (partitionSize < minimumSize) {
            throw new IllegalArgumentException("Partition size must be > " + minimumSize);
        }
    }
}
