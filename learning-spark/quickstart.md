Working on https://spark.apache.org/docs/1.2.0/quick-start.html, w/ a twist to make sure I'm learning it.

#Part I: Spark Shell

##Intro: Project Setup
By now you should have all the things installed. **TODO: Write instructions**

Note: By default, the shell will launch from the directory in which you run `spark-shell`. The Apache "quick start" instructs you to run the shell from the main directory of your spark installation (i.e. `./bin/spark-shell`), but it's reasonably likely that your command will differ based on your machine's setup and how you installed spark. If you used brew to install, for example, the command would be `./usr/local/spark-shell` or `/usr/local/spark-shell`. You can create an alias for these in your bash profile.

We'll set up a new project directory and work from there. Create a directory called HelloSpark (or whatever you want to call this ) and cd into it.
```bash
mkdir HelloSpark
```

Let's grab a big text file so that we can start playing with spark-shell! Following the hadoop intro assignment from a course I'm taking -- see mmds at https://lagunita.stanford.edu/courses/course-v1:ComputerScience+MMDS+Fall2016/about -- I'll user the complete works of Shakespeare, but feel free to choose any file you want. To get the Shakespeare file, run the following in your terminal:
```bash
`curl http://www.gutenberg.org/cache/epub/100/pg100.txt | perl -pe 's/^\xEF\xBB \xBF//' > input.txt`
```

NOTE: The following is all based on / largely taken from the quickstart, with edits and clarifications where applicable.
##A. Basics
* Spark’s primary abstraction is a distributed collection of items called a Resilient Distributed Dataset (RDD). 
    - RDDs can be created from Hadoop InputFormats (such as HDFS files) or by transforming other RDDs. 
    - For example, to create a new RDD from an input file (which in our case contains the complete works of Shakespeare), you could
```scala
scala> val shakespeare = sc.shakespeare("input.txt")
shakespeare: spark.RDD[String] = spark.MappedRDD@2ee9b6e3
```
    - RDDs have *actions* (which return values) and *transformations* (which return pointers to new RDDs)
* Actions return values. For example
```
scala> shakespeare.count() // Number of items in this RDD
res0: Long = 126

scala> shakespeare.first() // First item in this RDD
res1: String = # Apache Spark
```
* Transformations return pointers to new RDDs. For example
```scala
scala> val romeo = shakespeare.filter(line => line.contains("Romeo"))
linesWithSpark: spark.RDD[String] = spark.FilteredRDD@7dd4af09
```


FOOTNOTES
- Prevent local derby and metastoreDB: https://stackoverflow.com/questions/38377188/how-to-get-rid-of-derby-log-metastore-db-from-spark-shell