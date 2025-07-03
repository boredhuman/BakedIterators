# Overview
This is a simple benchmark to compare the performance of the various ways to iterate over a sequence of elements.
It also uses a baked iterator which essentially unrolls a loop of runnables into a sequential list of invocations.
Any perf gains the baked iterator has is likely due to its branchless nature. 

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

| Benchmark                                         | (size)   | Mode    | Cnt   | Score          | Error            | Units   |
|---------------------------------------------------|----------|---------|-------|----------------|------------------|---------|
| BakedArrayBenchmark.bakedArrayBenchmark           | 8        | thrpt   | 3     | 32652171.978   | ±  5425145.062   | ops/s   |
| ArrayListBenchmark.arrayListBasicForLoop          | 8        | thrpt   | 3     | 22374273.804   | ± 21557393.761   | ops/s   |
| PrimitiveArrayBenchmark.primitiveArrayBenchmark   | 8        | thrpt   | 3     | 22367779.475   | ± 10423958.819   | ops/s   |
| ArrayListBenchmark.arrayListEnchancedForLoop      | 8        | thrpt   | 3     | 21233856.789   | ±  9978688.204   | ops/s   |
| BakedArrayBenchmark.bakedArrayBenchmark           | 16       | thrpt   | 3     | 16521258.577   | ±  1374222.037   | ops/s   |
| ArrayListBenchmark.arrayListBasicForLoop          | 16       | thrpt   | 3     | 11270945.099   | ± 10512273.133   | ops/s   |
| PrimitiveArrayBenchmark.primitiveArrayBenchmark   | 16       | thrpt   | 3     | 11212898.783   | ±  5058293.343   | ops/s   |
| ArrayListBenchmark.arrayListEnchancedForLoop      | 16       | thrpt   | 3     | 10629483.778   | ±  3521635.697   | ops/s   |
| BakedArrayBenchmark.bakedArrayBenchmark           | 32       | thrpt   | 3     | 8299264.545    | ±  1089232.008   | ops/s   |
| ArrayListBenchmark.arrayListBasicForLoop          | 32       | thrpt   | 3     | 5681040.168    | ±  5332821.717   | ops/s   |
| PrimitiveArrayBenchmark.primitiveArrayBenchmark   | 32       | thrpt   | 3     | 5603789.635    | ±  2581700.253   | ops/s   |
| ArrayListBenchmark.arrayListEnchancedForLoop      | 32       | thrpt   | 3     | 5342639.993    | ±  2510691.470   | ops/s   |
| BakedArrayBenchmark.bakedArrayBenchmark           | 64       | thrpt   | 3     | 4184487.889    | ±   331036.959   | ops/s   |
| ArrayListBenchmark.arrayListBasicForLoop          | 64       | thrpt   | 3     | 2838461.693    | ±  2670428.583   | ops/s   |
| PrimitiveArrayBenchmark.primitiveArrayBenchmark   | 64       | thrpt   | 3     | 2794514.215    | ±  1171577.691   | ops/s   |
| ArrayListBenchmark.arrayListEnchancedForLoop      | 64       | thrpt   | 3     | 2682274.981    | ±  1205079.793   | ops/s   |

The different flavours of array baker all seem to have the same performance and therefore will not have their own 
table of results. Although this may not be true for all JVMs.

