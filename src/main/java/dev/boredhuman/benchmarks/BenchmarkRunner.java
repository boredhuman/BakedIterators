package dev.boredhuman.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenchmarkRunner {
	public static void main(String[] args) throws Throwable {
		Options options = new OptionsBuilder()
			.include(SwitchingArrayBenchmark.class.getSimpleName())
			.include(BakedArrayBenchmark.class.getSimpleName())
			.include(PrimitiveArrayBenchmark.class.getSimpleName())
			.include(ArrayListBenchmark.class.getSimpleName())
			.param("size", "16")
			.param("bakeType", "LOCAL")
			.forks(1)
			.warmupIterations(1)
			.warmupTime(TimeValue.seconds(3))
			.measurementIterations(3)
			.measurementTime(TimeValue.seconds(3))
			.build();

		new Runner(options).run();
	}
}
