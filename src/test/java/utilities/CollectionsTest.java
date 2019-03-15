package utilities;

import org.junit.Test;
import testutils.Person;
import types.Pair;
import types.Tuple;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static utilities.Collections.applyPredicate;
import static utilities.Collections.discardingPartition;
import static utilities.Collections.fill;
import static utilities.Collections.find;
import static utilities.Collections.findIndex;
import static utilities.Collections.first;
import static utilities.Collections.flatten;
import static utilities.Collections.head;
import static utilities.Collections.indexed;
import static utilities.Collections.indexedFrom;
import static utilities.Collections.indicesOf;
import static utilities.Collections.init;
import static utilities.Collections.isNullOrEmpty;
import static utilities.Collections.last;
import static utilities.Collections.mid;
import static utilities.Collections.newCollection;
import static utilities.Collections.notNullOrEmpty;
import static utilities.Collections.partition;
import static utilities.Collections.partitionNoSingletons;
import static utilities.Collections.partitionOverlapping;
import static utilities.Collections.reverse;
import static utilities.Collections.slice;
import static utilities.Collections.tail;
import static utilities.Collections.toOverlappingPairs;
import static utilities.Collections.toPairs;
import static utilities.Collections.unzip;
import static utilities.Collections.zip;
import static utilities.Collections.zipToMap;
import static utilities.Predicates.even;
import static utilities.Predicates.odd;

public class CollectionsTest {

    final private Person alice = new Person("Alice", 8);
    final private Person bob = new Person("Bob", 16);
    final private Person carol = new Person("Carol", 24);
    final private Collection<Person> people = asList(alice, bob, carol);

    final private Collection<Integer> oneToFive = asList(1, 2, 3, 4, 5);
    final private Collection<Integer> oneToSix = asList(1, 2, 3, 4, 5, 6);
    final private Collection<Integer> oneToNine = asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    final private Collection<String> words = asList("one", "two", "three");

    @Test
    public void testNew() {
        assertThat(newCollection(), is(emptyList()));
        assertThat(newCollection(1, 2, 3, 4, 5), is(oneToFive));
    }

    @Test
    public void testFlatten() {
        assertThat(flatten(newCollection(), newCollection()), is(newCollection()));
        assertThat(flatten(newCollection(1, 2), newCollection(3), newCollection(4, 5)), is(oneToFive));
        assertThat(flatten(newCollection(1), newCollection(), newCollection(2, 3, 4, 5), newCollection(6)), is(oneToSix));
    }

    @Test
    public void testFill() {
        assertThat(fill("foo", 0), is(newCollection()));
        assertThat(fill("foo", 2), is(newCollection("foo", "foo")));
        assertThat(fill("foo", 5), is(newCollection("foo", "foo", "foo", "foo", "foo")));

        assertThat(fill(() -> "f" + "o" + "o", 5), is(newCollection("foo", "foo", "foo", "foo", "foo")));
        final AtomicInteger counter = new AtomicInteger(1);
        assertThat(fill(() -> "foo" + counter.getAndIncrement(), 5), is(newCollection("foo1", "foo2", "foo3", "foo4", "foo5")));
    }

    @Test
    public void testValidators() {
        assertThat(notNullOrEmpty(oneToFive), is(true));
        assertThat(isNullOrEmpty(oneToFive), is(false));

        assertThat(notNullOrEmpty(null), is(false));
        assertThat(isNullOrEmpty(null), is(true));

        assertThat(notNullOrEmpty(newCollection()), is(false));
        assertThat(isNullOrEmpty(newCollection()), is(true));
    }

    @Test
    public void testToPairsValid() {
        assertThat(toPairs(oneToSix), contains(
                new Pair<>(1, 2),
                new Pair<>(3, 4),
                new Pair<>(5, 6)
        ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToPairsInvalid() {
        toPairs(oneToFive);
    }

    @Test
    public void testToOverlappingPairs() {
        assertThat(toOverlappingPairs(oneToFive), contains(
                new Pair<>(1, 2),
                new Pair<>(2, 3),
                new Pair<>(3, 4),
                new Pair<>(4, 5)
        ));
    }

    @Test
    public void testZeroIndexed() {
        assertThat(indexed(words), contains(
                new Tuple<>(0, "one"),
                new Tuple<>(1, "two"),
                new Tuple<>(2, "three")
        ));
    }

    @Test
    public void testOneIndexed() {
        assertThat(indexedFrom(words, 1), contains(
                new Tuple<>(1, "one"),
                new Tuple<>(2, "two"),
                new Tuple<>(3, "three")
        ));
    }

    @Test
    public void testSlice() {
        assertThat(slice(oneToSix, 0, 0), is(newCollection()));
        assertThat(slice(oneToSix, 4, 5), is(newCollection(5)));
        assertThat(slice(oneToSix, 2, 4), is(newCollection(3, 4)));
        assertThat(slice(oneToSix, 3, 6), is(newCollection(4, 5, 6)));
        assertThat(slice(oneToSix, 0, 4), is(newCollection(1, 2, 3, 4)));
    }

    @Test
    public void testSequenceMethods() {
        assertThat(first(oneToFive), is(1));
        assertThat(head(oneToFive), is(1));
        assertThat(tail(oneToFive), is(newCollection(2, 3, 4, 5)));
        assertThat(init(oneToFive), is(newCollection(1, 2, 3, 4)));
        assertThat(last(oneToFive), is(5));
        assertThat(mid(oneToFive), is(newCollection(2, 3, 4)));
    }

    @Test
    public void testFinds() {
        assertThat(find(people, p -> p.getName().equals("Alice")).get(), is(alice));
        assertThat(find(people, p -> p.getAge() == 16).get(), is(bob));
        assertThat(find(people, p -> p.getAge() > 20).get(), is(carol));
        assertThat(find(people, p -> p.getName().equalsIgnoreCase("eve")), is(Optional.empty()));

        assertThat(findIndex(people, p -> p.getName().equals("Alice")).getAsInt(), is(0));
        assertThat(findIndex(people, p -> p.getAge() == 16).getAsInt(), is(1));
        assertThat(findIndex(people, p -> p.getAge() > 20).getAsInt(), is(2));
        assertThat(findIndex(people, p -> p.getName().equalsIgnoreCase("eve")), is(OptionalInt.empty()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidZip() {
        zip(oneToFive, oneToSix);
    }

    @Test
    public void testValidZip() {
        assertThat(zip(newCollection("andrea", "ben", "cally"), newCollection(true, false, true)), contains(
                new Tuple<>("andrea", true),
                new Tuple<>("ben", false),
                new Tuple<>("cally", true)
        ));
    }

    @Test
    public void testZipToMap() {
        final Map<String, Boolean> zipped = zipToMap(newCollection("andrea", "ben", "cally"), newCollection(true, false, true));
        assertThat(zipped, hasEntry("andrea", true));
        assertThat(zipped, hasEntry("ben", false));
        assertThat(zipped, hasEntry("cally", true));
    }

    @Test
    public void testUnzip() {
        assertThat(unzip(newCollection(new Tuple<>("andrea", true), new Tuple<>("ben", false), new Tuple<>("cally", true))), is(
                new Tuple<>(newCollection("andrea", "ben", "cally"), newCollection(true, false, true))
        ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPartition() {
        partition(oneToSix, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDiscardingPartition() {
        discardingPartition(oneToSix, 0);
    }

    @Test
    public void testPartition() {
        assertThat(partition(oneToSix, 1), contains(newCollection(1), newCollection(2), newCollection(3), newCollection(4), newCollection(5), newCollection(6)));
        assertThat(partition(oneToSix, 2), contains(newCollection(1, 2), newCollection(3, 4), newCollection(5, 6)));
        assertThat(partition(oneToSix, 3), contains(newCollection(1, 2, 3), newCollection(4, 5, 6)));
        assertThat(partition(oneToSix, 4), contains(newCollection(1, 2, 3, 4), newCollection(5, 6)));
        assertThat(partition(oneToSix, 5), contains(newCollection(1, 2, 3, 4, 5), newCollection(6)));
        assertThat(partition(oneToSix, 6), contains(oneToSix));
    }

    @Test
    public void testDiscardingPartition() {
        assertThat(discardingPartition(oneToSix, 1), contains(newCollection(1), newCollection(2), newCollection(3), newCollection(4), newCollection(5), newCollection(6)));
        assertThat(discardingPartition(oneToSix, 2), contains(newCollection(1, 2), newCollection(3, 4), newCollection(5, 6)));
        assertThat(discardingPartition(oneToSix, 3), contains(newCollection(1, 2, 3), newCollection(4, 5, 6)));
        assertThat(discardingPartition(oneToSix, 4), contains(newCollection(1, 2, 3, 4)));
        assertThat(discardingPartition(oneToSix, 5), contains(newCollection(1, 2, 3, 4, 5)));
        assertThat(discardingPartition(oneToSix, 6), contains(oneToSix));
    }

    @Test
    public void testPartitionNoSingleton() {
        final Collection<Collection<Integer>> of3 = partitionNoSingletons(oneToNine, 3);
        assertThat(of3, hasSize(3));
        assertThat(of3, contains(newCollection(1, 2, 3), newCollection(4, 5, 6), newCollection(7, 8, 9)));

        final Collection<Collection<Integer>> of4 = partitionNoSingletons(oneToNine, 4);
        assertThat(of4, hasSize(3));
        assertThat(of4, contains(newCollection(1, 2, 3, 4), newCollection(5, 6, 7), newCollection(8, 9)));

        final Collection<Collection<Integer>> of8 = partitionNoSingletons(oneToNine, 8);
        assertThat(of8, hasSize(2));
        assertThat(of8, contains(newCollection(1, 2, 3, 4, 5, 6, 7), newCollection(8, 9)));
    }

    @Test
    public void testPartitionOverlapping() {
        final Collection<Collection<Integer>> of3 = partitionOverlapping(oneToNine, 3);
        assertThat(of3, hasSize(4));
        assertThat(of3, contains(newCollection(1, 2, 3), newCollection(3, 4, 5), newCollection(5, 6, 7), newCollection(7, 8, 9)));

        final Collection<Collection<Integer>> of4 = partitionOverlapping(oneToNine, 4);
        assertThat(of4, hasSize(3));
        assertThat(of4, contains(newCollection(1, 2, 3, 4), newCollection(4, 5, 6, 7), newCollection(7, 8, 9)));

        final Collection<Collection<Integer>> of8 = partitionOverlapping(oneToNine, 8);
        assertThat(of8, hasSize(2));
        assertThat(of8, contains(newCollection(1, 2, 3, 4, 5, 6, 7, 8), newCollection(8, 9)));

        // do the other permutations of 1-9 in less detail
        assertThat(partitionOverlapping(oneToNine, 2), contains(newCollection(1, 2), newCollection(2, 3), newCollection(3, 4), newCollection(4, 5), newCollection(5, 6), newCollection(6, 7), newCollection(7, 8), newCollection(8, 9)));
        assertThat(partitionOverlapping(oneToNine, 5), contains(newCollection(1, 2, 3, 4, 5), newCollection(5, 6, 7, 8, 9)));
        assertThat(partitionOverlapping(oneToNine, 6), contains(newCollection(1, 2, 3, 4, 5, 6), newCollection(6, 7, 8, 9)));
        assertThat(partitionOverlapping(oneToNine, 7), contains(newCollection(1, 2, 3, 4, 5, 6, 7), newCollection(7, 8, 9)));
        assertThat(partitionOverlapping(oneToNine, 9), contains(newCollection(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }

    @Test
    public void testReverse() {
        assertThat(reverse(oneToFive), is(newCollection(5, 4, 3, 2, 1)));
    }

    @Test
    public void testIndicesOf() {
        assertThat(indicesOf(newCollection("foo", "bar", "baz"), "foo"), is(newCollection(0)));
        assertThat(indicesOf(newCollection("foo", "bar", "baz"), "qux"), is(newCollection()));
        assertThat(indicesOf(newCollection("foo", "bar", "baz", "bar"), "bar"), is(newCollection(1, 3)));
    }

    @Test
    public void testApplyPredicate() {
        assertThat(applyPredicate(oneToSix, even()), is(newCollection(2, 4, 6)));
        assertThat(applyPredicate(oneToSix, odd()), is(newCollection(1, 3, 5)));
    }
}
