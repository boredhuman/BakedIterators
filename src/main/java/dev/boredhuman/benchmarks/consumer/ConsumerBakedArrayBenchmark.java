package dev.boredhuman.benchmarks.consumer;

import dev.boredhuman.BakeTypes;
import dev.boredhuman.ConsumerArrayBaker;
import dev.boredhuman.VariableStorage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Consumer;

@State(Scope.Benchmark)
public class ConsumerBakedArrayBenchmark {
	@Param({ "8" })
	private int size;
	@Param
	private BakeTypes bakeType;
	@Param
	private VariableStorage variableStorage;
	private Consumer<Object> runner;

	@Benchmark
	public void bakedArrayBenchmark() {
		Object object = new Object();
		this.runner.accept(object);
	}

	@Setup
	public void setup(Blackhole blackhole) {
		Consumer[] tasks = new Consumer[this.size];

		for (int i = 0; i < this.size; i++) {
			tasks[i] = blackhole::consume;
		}

		this.runner = new ConsumerArrayBaker().bake(tasks, this.bakeType, this.variableStorage);
	}
}
