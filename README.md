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

## Results

The results can be found in the results folder. The name of the file indicates what benchmark was ran and with what 
java version.

