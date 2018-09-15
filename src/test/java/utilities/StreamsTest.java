package utilities;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static utilities.Collections.newCollection;
import static utilities.Streams.fromOptionals;
import static utilities.Streams.reverseRange;
import static utilities.Streams.reverseRangeClosed;


public class StreamsTest {

    @Test
    public void testReverseRange() {
        assertThat(collect(reverseRange(0, 5)), is(newCollection(4, 3, 2, 1, 0)));
    }

    @Test
    public void testReverseRangeClosed() {
        assertThat(collect(reverseRangeClosed(0, 5)), is(newCollection(5, 4, 3, 2, 1, 0)));
    }

    @Test
    public void testFromOptionals() {
        assertThat(fromOptionals(newCollection(Optional.of("foo"), Optional.empty(), Optional.of("bar"))).collect(Collectors.toList()), contains("foo", "bar"));
    }

    private Collection<Integer> collect(final IntStream intStream) {
        return intStream.collect(ArrayList::new, Collection::add, Collections::flatten);
    }
}
