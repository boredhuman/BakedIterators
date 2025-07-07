package dev.boredhuman.benchmarks.runners;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class ConsumerBenchmarkRunner {
	public static void main(String[] args) throws Throwable {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion == null || javaVersion.isEmpty()) {
			throw new RuntimeException("Failed to query java version from system property using java.version");
		}

		Options options = new OptionsBuilder()
			.include(".*benchmarks\\.consumer\\..*")
			.param("size", "16")
			.param("bakeType", "LOCAL")
			.param("variableStorage", "STATIC")
			.resultFormat(ResultFormatType.TEXT)
			.result("results/consumer-benchmarks-" + javaVersion + ".txt")
			.forks(1)
			.warmupIterations(1)
			.warmupTime(TimeValue.seconds(3))
			.measurementIterations(3)
			.measurementTime(TimeValue.seconds(3))
			.build();

		new Runner(options).run();
	}
}
