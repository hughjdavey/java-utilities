package utilities;

import org.junit.Test;
import testutils.Widget;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static utilities.Collections.applyPredicate;
import static utilities.Collections.newCollection;
import static utilities.Predicates.and;
import static utilities.Predicates.empty;
import static utilities.Predicates.even;
import static utilities.Predicates.not;
import static utilities.Predicates.odd;
import static utilities.Predicates.or;
import static utilities.Predicates.xor;

public class PredicatesTest {

    final private Widget purpleWidget = new Widget(true, false);
    final private Widget shinyWidget = new Widget(false, true);
    final private Widget purpleAndShinyWidget = new Widget(true, true);
    final private Widget plainWidget = new Widget(false, false);

    final private Collection<Widget> widgets = newCollection(purpleWidget, shinyWidget, purpleAndShinyWidget, plainWidget);

    @Test
    public void testAnd() {
        assertThat(applyPredicate(widgets, and(Widget::isPurple, Widget::isShiny)), is(newCollection(purpleAndShinyWidget)));
    }

    @Test
    public void testNot() {
        assertThat(applyPredicate(widgets, not(Widget::isShiny)), is(newCollection(purpleWidget, plainWidget)));
    }

    @Test
    public void testOr() {
        assertThat(applyPredicate(widgets, or(Widget::isPurple, Widget::isShiny)), is(newCollection(purpleWidget, shinyWidget, purpleAndShinyWidget)));
    }

    @Test
    public void testXor() {
        assertThat(applyPredicate(widgets, xor(Widget::isPurple, Widget::isShiny)), is(newCollection(purpleWidget, shinyWidget)));
    }

    @Test
    public void testEven() {
        assertThat(applyPredicate(newCollection(1, 2, 3, 4, 5, 6), even()), is(newCollection(2, 4, 6)));
    }

    @Test
    public void testOdd() {
        assertThat(applyPredicate(newCollection(1, 2, 3, 4, 5, 6), odd()), is(newCollection(1, 3, 5)));
    }

    @Test
    public void testEmpty() {
        assertThat(applyPredicate(newCollection(newCollection(), newCollection("foo"), newCollection()), not(empty())), is(newCollection(newCollection("foo"))));
    }
}
