NOTE: Testing https://atom.io/packages/markdown-preview-plus

#CHAPTER 2: MapReduce and the New Software Stack

##Intro
* We need to process ever-larger data sets, ever more quickly.
* To do that, the new software stack derives its *parallelism* from "computing clusters" — large collections of hardware, including conventional processors (nodes).
  - TL;DR, many connected computers > one supercomputer.
* Need a new form of file system -- a *distributed file system* — to manage the cluster.
  - a dfs contains larger units than a conventional OS's disk blocks
  - a dfs also provides redundancy (for inevitable copying errors that occur w/ distributed data)
* How to do work on top of a distributed file system? MapReduce
  - bunch of diff't implementations, current tech is changing (NTS: see spark/scala, which have risen in popularity since book's publication)

##2.1 - Distributed File Systems
* Most common computing done on a single processor -- its main memory, cache, and local disk. When you need to do parallel processing (for more complex calculations),
  - old way: special-purpose parallel computers w/ many processors, special hardware
  - new way: combine tens/hundreds/thousands of cheap, normal computers.
* But you need a new programming system to handle that many connected computers
  - must (1) parallelize efficiently, (2) handle potential failures

###i. Cluster Computing - Physical Architecture
Nodes => Racks => Switch
* Compute nodes are stored on *racks* (eg 8-64 gb Ethernet)
* Racks are connected by another level of network or switch

How do we handle failures? First, note that most failures = node failures. We handle those by (1) storing files redundantly (ie in multiple nodes), and (2) dividing computations into tasks, s/t if any one task (node) fails it can be restarted w/o affecting other tasks.

###ii. Distributed File System Organization
* Several types are used in practice:
  1. Google File System (GFS) - original
  2. Hadoop Distributed File System (HDFS) - open-source DFS
* Files can be huge. If they're small, there's no point using a DFS for them.
* Files are rarely updated. Instead, they're read for calculation.
* Files are divided into *chunks*, typically 64MB.
  - Chunks are replicated (maybe 3x) across compute nodes
  - To find the chunks of a file, there is another small file called the master node or name node

##2.2 - MapReduce
No matter the implementation (HDFS, GFS, etc.), all you need to write are two functions, called Map and Reduce. The file system manages the parallel execution, coordination of tasks that execute Map or Reduce, and also deals with the possibility that one of these tasks will fail to execute.
* How it works:
  1. Some number of *Map tasks* each are given one or more chunks of data from a dfs. Each Map task turns its chunk(s) into a sequence of key-value pairs. (The programmer specifies how the chunk's data translates to k-v.)
  2. A *master controller* collects the k-v pairs from each Map task and sorts them by key.
  3. The keys are divided among all the *Reduce tasks* s/t all k-v pairs with the same key wind up at the same Reduce task.
  4. The Reduce tasks work on one key at a time, combining all values associated w/ that key in some way. (Again, the programmer specifies how values are combined.)

###i. MR PART 1: Map tasks
The Map function takes an input element as its argument and produces 0 or more k-v pairs. The input element can be of any type, e.g. a document or an array; the types of keys and values are arbitrary. Further, they "keys" are not really keys, in that they need not be unique.

####Example: Counting Words Across Documents
* The input file is a repository of documents
* Each document is an element
* The Map function uses keys that are of type String (the words) and values that are Integer (the counts)
* The Map task reads a document and breaks it into its sequence of words *w1, w2,...,wn*
  - That is, the output of the Map task for this document will be the sequence of k-v pairs
  ```
  (w1, 1), (w2, 1), ...(wn, 1)
  ```
* Note that a single Map task will typically process many documents, grouped together in one or more chunks.
  - This means that if a word "foo" appears 5 times, once in each document, then the output will be *5* instances of ("foo", 1)
  - There's an option to combine these into ("foo", 5). **see section iv (below)**

###ii. MR PART 2: Grouping by Key
* After Map tasks are completed, the dfs combines values associated w/ each key into a single list of values. For example, ("foo", "bar") and ("foo", "baz") => ("foo", ["bar", "baz"]) (kinda -- but it'll do for now)
* The master controller picks a hash function that applies to keys and produces a bucket number *b* from 0 - *r*, where *r* is the number of Reduce tasks
  - (user typically tells MapReduce what *r* should be)
  - Note that this means each key is processed by one and only one Reduce task
* The master controller then merges together all files destined for Reduce task *b*. The merged files contain keys and values in the form of `(k, [v1, v2,...,vn])`.
* The Reduce task will process each (k0,[v0...va])...(kn,[v0...vb]) in order

###iii. MR PART 3: Reduce Tasks
So we see that a Reduce function's argument is a pair consisting of a key and its list of associated values.

* The output of a the Reduce function is a sequence of 0 or more k-v pairs.   
  - (The application of a Reduce function to a single key-list is a *reducer*.)
  - A single Reduce task has many reducers
* The outputs from all Reduce tasks are merged into a single file.
* Reducers may be partitioned into several reduce tasks by hashing the keys into buckets, where each reduce task is responsible for a bucket

####Example: Words Across Documents (Reducer)
In the word counting example, the reduce function adds up values.
  - The output of a single reducer is the word and its sum.
  - The output of a Reduce task is a partial sequence of (word, count) pairs
  - The output of all Reduce tasks is a sequence of (word, count) pairs

##iv. MR PART 4: Combiners
* When a reduce fn is associative and commutative -- that is, the values can be combined, and combined in any order (respectively) -- we can push some "reducing" to the Map task
  - e.g. In the counting words example, we can apply a reduce fn within the Map task, *before* the output of the Map is grouped and aggregated by the reducer
    * The k-v pairs for a word (*w*, 1) would be replaced/summed up into one key *w* and value equal to the sum of all 1's in those pairs
    * In other words, the multiple pairs (*w*, 1) => (*w*, *m*)
* You'll still need to group / aggregate across documents (i.e. apply the reducer in a separate step). However, a good chunk of the aggregation will already have been completed.
  - QUESTION: What's the advantage here? The reducer will be faster, but at the expense of a slower map -- so what's the point? Does one win out?

###v. MR Execution Details

##2.3 Algorithms Using MapReduce
###i. Matrix-Vector Multiplication
* Suppose *n \times n* matrix *M*, whose element in row *i* and column *j* = *m<sub>ij</sub>*.
* Example, pagerank
