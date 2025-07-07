package dev.boredhuman.benchmarks.runnable;

import dev.boredhuman.RunnableArrayBaker;
import dev.boredhuman.BakeTypes;
import dev.boredhuman.VariableStorage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class BakedArrayBenchmark {
	@Param({ "8" })
	private int size;
	@Param
	private BakeTypes bakeType;
	@Param
	private VariableStorage variableStorage;
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

		this.tasksRunner = new RunnableArrayBaker().bake(tasks, this.bakeType, this.variableStorage);
	}
}
