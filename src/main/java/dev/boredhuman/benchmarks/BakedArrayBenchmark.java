package dev.boredhuman.benchmarks;

import dev.boredhuman.ArrayBaker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class BakedArrayBenchmark {
	@Param({ "8", "16", "32", "64" })
	private int size;
	private Runnable tasksRunner;

	@Benchmark
	public void bakedArrayBenchmark() {
		this.tasksRunner.run();
	}

	@Setup
	public void setup(Blackhole blackhole) {
		Runnable[] tasks = new Runnable[this.size];
		for (int i = 0; i < this.size; i++) {
			int copy = i;
			tasks[i] = () -> blackhole.consume(copy);
		}

		this.tasksRunner = new ArrayBaker().bake(tasks);
	}
}
