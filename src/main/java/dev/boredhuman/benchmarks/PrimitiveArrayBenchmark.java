package dev.boredhuman.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class PrimitiveArrayBenchmark {
	@Param({ "8", "16", "32", "64" })
	private int size;
	private Runnable[] tasks;

	@Benchmark
	public void primitiveArrayBenchmark() {
		for (Runnable task : this.tasks) {
			task.run();
		}
	}

	@Setup
	public void setup(Blackhole blackhole) {
		this.tasks = new Runnable[this.size];
		for (int i = 0; i < this.size; i++) {
			int copy = i;
			this.tasks[i] = () -> blackhole.consume(copy);
		}
	}
}
