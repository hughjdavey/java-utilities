package types;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PairAndTupleTest {

    @Test
    public void testPair() {
        final Pair<String> pair = new Pair<>("1", "one");
        assertThat(pair.getOne(), is("1"));
        assertThat(pair._1(), is("1"));
        assertThat(pair.getTwo(), is("one"));
        assertThat(pair._2(), is("one"));
    }

    @Test
    public void testTuple() {
        final Tuple<Integer, String> tuple = new Tuple<>(1, "one");
        assertThat(tuple.getOne(), is(1));
        assertThat(tuple._1(), is(1));
        assertThat(tuple.getTwo(), is("one"));
        assertThat(tuple._2(), is("one"));
    }
}
