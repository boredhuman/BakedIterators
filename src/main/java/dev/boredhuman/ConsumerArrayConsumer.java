package dev.boredhuman;

import java.util.function.Consumer;

public interface ConsumerArrayConsumer<T> {
	void accept(Consumer<T>[] consumer, T instance);
}
