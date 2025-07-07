package dev.boredhuman.benchmarks.consumer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Consumer;

@State(Scope.Benchmark)
public class ConsumerPrimitiveArrayBenchmark {
	@Param({ "8" })
	private int size;
	private Consumer[] consumers;

	@Benchmark
	public void benchmark() {
		Object object = new Object();
		for (Consumer consumer : this.consumers) {
			consumer.accept(object);
		}
	}

	@Setup
	public void setup(Blackhole blackhole) {
		this.consumers = new Consumer[this.size];

		for (int i = 0; i < this.size; i++) {
			this.consumers[i] = (item) -> blackhole.consume(item);
		}
	}
}
