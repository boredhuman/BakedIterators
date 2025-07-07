package dev.boredhuman.benchmarks.runners;

import dev.boredhuman.benchmarks.consumer.ConsumerBakedArrayBenchmark;
import dev.boredhuman.benchmarks.consumer.ConsumerPrimitiveArrayBenchmark;
import dev.boredhuman.benchmarks.consumer.SwitchingConsumerArrayBenchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class ConsumerBenchmarkRunner {
	public static void main(String[] args) throws Throwable {
		Options options = new OptionsBuilder()
			.include(ConsumerBakedArrayBenchmark.class.getSimpleName())
			.include(ConsumerPrimitiveArrayBenchmark.class.getSimpleName())
			.include(SwitchingConsumerArrayBenchmark.class.getSimpleName())
			.param("size", "16")
			.param("bakeType", "LOCAL")
			.param("variableStorage", "STATIC")
			.forks(1)
			.warmupIterations(1)
			.warmupTime(TimeValue.seconds(3))
			.measurementIterations(3)
			.measurementTime(TimeValue.seconds(3))
			.build();

		new Runner(options).run();
	}
}
