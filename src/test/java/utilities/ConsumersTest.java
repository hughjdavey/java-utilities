package utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConsumersTest {

    @Mock
    private Consumer<Integer> intConsumer;

    @Test
    @SuppressWarnings(value = "unchecked")
    public void testRepeat() {
        // test called right number of times and only when > 0
        Consumers.repeat(5, intConsumer);
        verify(intConsumer, times(5)).accept(anyInt());
        reset(intConsumer);
        Consumers.repeat(-5, intConsumer);
        verify(intConsumer, never()).accept(anyInt());
        reset(intConsumer);
        Consumers.repeat(0, intConsumer);
        verify(intConsumer, never()).accept(anyInt());

        // test index is correct
        final AtomicInteger count = new AtomicInteger(0);
        Consumers.repeat(5, index -> {
            assertThat(index, is(count.getAndIncrement()));
        });
    }
}
