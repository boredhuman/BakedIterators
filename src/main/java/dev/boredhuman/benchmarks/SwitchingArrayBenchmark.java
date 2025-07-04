package dev.boredhuman.benchmarks;

import dev.boredhuman.RunnableConsumer;
import dev.boredhuman.SwitchingArrayBaker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SwitchingArrayBenchmark {
	@Param({ "8" })
	private int size;
	private Runnable[] tasks;
	private RunnableConsumer tasksRunner;

	@Benchmark
	public void bakedArrayBenchmark() {
		this.tasksRunner.accept(this.tasks);
	}

	@Setup
	public void setup(Blackhole blackhole) {
		Runnable[] tasks = new Runnable[this.size];
		for (int i = 0; i < this.size; i++) {
			int copy = i;
			tasks[i] = () -> blackhole.consume(copy);
		}

		this.tasks = tasks;
		this.tasksRunner = new SwitchingArrayBaker().bake(size);
	}
}
