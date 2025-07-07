package dev.boredhuman.benchmarks.consumer;

import dev.boredhuman.ConsumerArrayConsumer;
import dev.boredhuman.ConsumerArrayConsumerGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Consumer;

@State(Scope.Benchmark)
public class SwitchingConsumerArrayBenchmark {
	@Param({ "8" })
	private int size;
	private Consumer[] consumers;
	private ConsumerArrayConsumer runner;

	@Benchmark
	public void bakedArrayBenchmark() {
		Object object = new Object();
		this.runner.accept(this.consumers, object);
	}

	@Setup
	public void setup(Blackhole blackhole) {
		Consumer[] consumers = new Consumer[this.size];
		for (int i = 0; i < this.size; i++) {
			consumers[i] = blackhole::consume;
		}

		this.consumers = consumers;
		this.runner = new ConsumerArrayConsumerGenerator().generate(this.size);
	}
}
