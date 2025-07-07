# Overview
This is a simple benchmark to compare the performance of the various ways to iterate over a sequence of elements.
This benchmark also experiments with iterators which are unrolled variants of the normal looping code. An unrolled
loop is one where the loop body is duplicated X times. X being the count of the iteration.

## Considerations
This benchmark only provides conclusive results relative to the types in this benchmark. For example the
performance of array list may be better or worse than the other implementations of the list interface.
Furthermore, the baked implementation can be changed in various ways which may result in potential perf gains 
/ loses. For example, the dup instructions can all be done at the start instead of maintaining a single
reference on the stack which is constantly duplicated. 

## Setup
CPU: Ryzen 5 3600<br>
RAM: 16G 3600MHz<br>
VM version: JDK 1.8.0_432, OpenJDK 64-Bit Server VM, 25.432-b06

## Results
Sorted from ascending to descending

| Benchmark                                       | (bakeType) | (size) | Mode  | Cnt | Score        | Error         | Units |
|-------------------------------------------------|------------|--------|-------|-----|--------------|---------------|-------|
| ArrayListBenchmark.arrayListBasicForLoop        | N/A        | 16     | thrpt | 3   | 10910621.166 | ± 9518688.259 | ops/s |
| ArrayListBenchmark.arrayListEnchancedForLoop    | N/A        | 16     | thrpt | 3   | 10216103.620 | ± 6060949.421 | ops/s |
| BakedArrayBenchmark.bakedArrayBenchmark         | LOCAL      | 16     | thrpt | 3   | 16010487.787 | ± 1527666.686 | ops/s |
| PrimitiveArrayBenchmark.primitiveArrayBenchmark | N/A        | 16     | thrpt | 3   | 10744211.558 | ± 5335949.901 | ops/s |
| SwitchingArrayBenchmark.bakedArrayBenchmark     | N/A        | 16     | thrpt | 3   | 15937537.472 | ± 1618313.020 | ops/s |

| Benchmark                                           | (bakeType) | (size) | (variableStorage) | Mode  | Cnt | Score        | Error         | Units |
|-----------------------------------------------------|------------|--------|-------------------|-------|-----|--------------|---------------|-------|
| ConsumerBakedArrayBenchmark.bakedArrayBenchmark     | LOCAL      | 16     | STATIC            | thrpt | 3   | 15662719.146 | ± 2041135.423 | ops/s |
| ConsumerPrimitiveArrayBenchmark.benchmark           | N/A        | 16     | N/A               | thrpt | 3   | 9889675.285  | ± 4909069.191 | ops/s |
| SwitchingConsumerArrayBenchmark.bakedArrayBenchmark | N/A        | 16     | N/A               | thrpt | 3   | 15626596.652 | ± 2814569.513 | ops/s |

The different flavours of array baker (BakerTypes) all seem to have the same performance and therefore will not have their own 
table of results. Although this may not be true for all JVMs.

