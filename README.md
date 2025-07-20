# FibonacciHeapProject
This project was developed as part of a data structures course at Tel Aviv University. It includes a full implementation of a Fibonacci Heap in Java — a priority queue data structure optimized for amortized performance.
The implementation supports the following operations: insert, findMin, delete-min, delete, meld, get-size, get-total-links, get-total-cuts, get-trees-count, and decrease-key.

In addition, the heap includes a configurable parameter c, which defines how many children must be cut from a node before triggering a cascading cut, allowing experimentation with generalized heap behavior beyond the standard Fibonacci Heap definition.

Special attention was given to the time and space complexity of all operations. The implementation ensures:

O(1) W.C time for insert, findMin, meld, get-size, get-total-links, get-total-cuts, and get-trees-count
O(1) amortized time for decrease-key
O(log n) amortized time for delete-min, and delete

Efficient structure maintenance via lazy consolidation and marking

This project demonstrates a deep understanding of advanced heap structures, and amortized analysis.

