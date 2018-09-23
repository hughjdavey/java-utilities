package utilities;

import java.util.function.Consumer;

public class Consumers {

    public static void repeat(final int times, final Consumer<Integer> consumer) {
        if (times > 0) {
            for (int i = 0; i < times; i++) {
                consumer.accept(i);
            }
        }
    }
}
