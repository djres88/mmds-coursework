Working on https://spark.apache.org/docs/1.2.0/quick-start.html, w/ a twist to make sure I'm learning it.

Project Prep: Get the complete works of Shakespeare at
`curl http://www.gutenberg.org/cache/epub/100/pg100.txt | perl -pe 's/^\xEF\xBB \xBF//' > pg100.txt`

#Part I: Spark Shell
Note: By default, the shell will launch from the directory in which you run `spark-shell`. The Apache "quick start" guide instructs you to run the shell from the root of the installation (i.e. `./bin/spark-shell`), but it's reasonably likely that your installation of spark lives elsewhere. If you used brew to install, for example, the command would be `./usr/local/spark-shell`, unless `/` refers to your $ user directory, in which case you'll want to `cd `

Instead, I'd recommend setting up a new project directory and working from there. We

##A. Basics
* Spark’s primary abstraction is a distributed collection of items called a Resilient Distributed Dataset (RDD). 
    - RDDs can be created from Hadoop InputFormats (such as HDFS files) or by transforming other RDDs. 
    - For example, to create a new RDD from an input file (which in our case contains the complete works of Shakespeare, you could
```scala
scala> val shakespeare = sc.shakespeare("pg100.txt")
shakespeare: spark.RDD[String] = spark.MappedRDD@2ee9b6e3
```
    - RDDs have *actions* (which return values) and *transformations* (which return pointers to new RDDs)
* Actions return values. For example

* Transformations return pointers to new RDDs. For example
```scala
scala> val romeo = shakespeare.filter(line => line.contains("Romeo"))
linesWithSpark: spark.RDD[String] = spark.FilteredRDD@7dd4af09
```