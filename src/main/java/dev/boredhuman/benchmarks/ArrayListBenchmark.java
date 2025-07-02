package dev.boredhuman.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class ArrayListBenchmark {
	@Param({ "8", "16", "32", "64" })
	private int size;
	private List<Runnable> tasks;

	@Benchmark
	public void arrayListEnchancedForLoop() {
		for (Runnable task : this.tasks) {
			task.run();
		}
	}

	@Benchmark
	public void arrayListBasicForLoop() {
		List<Runnable> tasks = this.tasks;
		for (int i = 0, len = tasks.size(); i < len; i++) {
			tasks.get(i).run();
		}
	}

	@Setup
	public void setup(Blackhole blackhole) {
		this.tasks = new ArrayList<>(this.size);
		for (int i = 0; i < this.size; i++) {
			int copy = i;
			tasks.add(() -> blackhole.consume(copy));
		}
	}
}
