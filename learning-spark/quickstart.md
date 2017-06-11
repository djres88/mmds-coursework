Working on https://spark.apache.org/docs/1.2.0/quick-start.html, w/ a twist to make sure I'm learning it.

# Part I: Spark Shell

## Intro
By now you should have all the things installed. **TODO: Write instructions**
### Shell Setup
Note: By default, the shell will launch from the directory in which you run `spark-shell`. The Apache "quick start" instructs you to run the shell from the main directory of your spark installation (i.e. `./bin/spark-shell`), but it's reasonably likely that your command will differ based on your machine's setup and how you installed spark. If you used brew to install, for example, the command would be `./usr/local/spark-shell` or `/usr/local/spark-shell`. Our project (see below) will attempt to be agnostic about your setup.

We'll start by creating a new project directory (anywhere you keep your projects) called HelloSpark. Run the following in your terminal:
```bash
mkdir HelloSpark
```
Cool beans. Next, let's test out spark-shell.
```bash
spark-shell
```
Great! You should see a bunch of logs from spark. Warnings are normal.

We're not quite ready to play around with the shell yet, though. Your setup worked this time, we need to see whether your setup will work next time, too. Quit the shell (CTRL+c), then examine the contents of your local directory:
```bash
ls
# metastore_db derby.log
```
If you don't see these files, feel free to move on to the project setup section.

These two files, metastore_db and derby.log, hold spark's (something or other -- sql-like db) for the session. If you start a new spark-shell session from this directory, it may complain that these files already exist; you'll see many more error logs than when you launched spark-shell for the first time from this directory. But even if you're not getting errors, it's a little annoying to have these extra spark files show up in any directory where you run spark-shell. They're not particularly important; you just want a REPL.

So let's configure spark to store these files somewhere more convenient. There are a couple of possible answers here:

The best first option is https://stackoverflow.com/a/44048667/6338746. Basically, you can configure your spark-defaults.conf file to store the metastore_db and derby.log elsewhere. That one didn't work for me, for reasons that I gave up on debugging. (If you're thinking, "But you need to remove the .template spark-defaults.conf.template" -- tried it.)

If that doesn't work for you, you can use a flag to pass a custom configuration when you launch spark. First, create a folder in your /tmp directory:
```bash
mkdir /tmp/derby
```
Next, add this to your .bashrc (or .zshrc):
```bash
alias spark-shell='spark-shell --conf spark.driver.extraJavaOptions=-Dderby.system.home=/tmp/derby'
```
Now, every time you run spark-shell, the metastore_db and derby.log files will be (temporarily) store in your tmp/derby file. No more messy, unnecessary files in your project directory!


### Project Setup
We're ready to set up a project. Let's grab a big text file so that we can start playing with spark-shell!

Following the hadoop intro assignment from a course I'm taking -- see mmds at https://lagunita.stanford.edu/courses/course-v1:ComputerScience+MMDS+Fall2016/about -- I'll use the complete works of Shakespeare. You should feel free to choose any file you want, though. Basketball play by play logs? Song lyrics? Abset strange formatting issues, any massive any text file will do.

To get the Shakespeare file, run the following in your terminal:
```bash
`curl http://www.gutenberg.org/cache/epub/100/pg100.txt | perl -pe 's/^\xEF\xBB \xBF//' > input.txt`
```

Now we have our input file. Let's see what's in there.

ATTRIBUTION NOTE: The following is all based on / largely taken from the quickstart, with edits and clarifications where applicable.

## A. Spark & `spark-shell` Basics
To start up the spark-shell, run `spark-shell`. You should see logs, followed by a command prompt (`>scala`). Minor warnings are normal, errors are not.

So what's here?
* Spark’s primary abstraction is a distributed collection of items called a *Resilient Distributed Dataset (RDD)*.
    - RDDs can be created from Hadoop InputFormats (such as HDFS files) or by transforming other RDDs.
    - For example, to create a new RDD from an input file (which in our case contains the complete works of Shakespeare), you could transform the input file to an RDD as follows:
```scala
scala> val shakespeare = sc.textFile("input.txt")
shakespeare: spark.RDD[String] = spark.MappedRDD@2ee9b6e3
```
* What can you do with an RDD?
    - RDDs have *actions* (which return values) and *transformations* (which return pointers to new RDDs)
    - Rough analogy: You can think of actions as "properties" and transformations as "methods", if that lingo seems more familiar
    - **Actions** return values. For example
```scala
scala> shakespeare.count() // Number of items in this RDD
res0: Long = 124787

scala> shakespeare.first() // First item in this RDD
res1: String = The Project Gutenberg EBook of The Complete Works of William Shakespeare, by
```
    - **Transformations** return pointers to new RDDs. For example

```scala
scala> val romeo = shakespeare.filter(line => line.contains("Romeo"))
linesWithSpark: spark.RDD[String] = spark.FilteredRDD@7dd4af09
scala> romeo.count()
res2: Long = 146
```

## B. More RDD Operations & MapReduce Intro
Find the line with the most words:
```scala
scala> shakespeare.map(line => line.split(" ").size).reduce((a, b) => if (a > b) a else b)
res3: Int = 61
```

Spark and Scala make super easy work of MapReduce. To find the counts of each word, we can run:
```scala
scala> val wordCounts = shakespeare.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
wordCounts: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[8] at reduceByKey at <console>:26
wordCounts.collect()
res4: Array[(String, Int)] = Array((hack'd.,1), (durst,,1), (Ah!,3), (bone,7), (Worthy;,1), (vailing,1), (bombast,1), (person-,1), (LAFEU],1), (fiction.,1), (signal.,1), (Friend,5), (hem,1), (stinks,1), (meets,,1), (shalt.,1), (fuller,2), (Hermione;,1), (Beaufort,,4), (jade,6), (countervail,1), (crying,23), (Sought,2), (intelligencer;,1), (breath,115), (discased,,1), (deep-brained,1), (reclaims,,1), (branches-it,1), (peppercorn,,1), (sinning.,1), (fowl,7), (coat;,3), (OLIVIA,8), (afterward,5), (soon;,1), (pass'd.,5), (harlot's,2), (despite.,2), (abroad-anon,1), (wicked?,4), (inquisition,1), (angels,,2), (unfelt,3), (speak:,4), (Man-ent'red,1), (overkind,1), (Abates,1), (burghers,2), (speaks;,3), (Thunder.,6), (feats;,1), (people!,4), (superscript:,1), (robin,1), (regina,1), (pains;,7), ...
```

To find the counts of words starting with each letter:
```scala
scala> val wordCounts = shakespeare.flatMap(line => line.split(" ")).map(word => (if(!word.isEmpty()) word.charAt(0), 1)).reduceByKey((a, b) => a + b)
scala> wordCounts.collect()
res8: Array[(AnyVal, Int)] = Array((T,21999), (d,23531), (z,53), (",356), (4,46), (8,15), (L,7216), (p,19344), (R,3865), (B,10894), (6,22), (P,8415), (t,101603), (.,52), (b,34561), (0,6), (h,50511), (2,95), ($,1), ((),506610), (n,21813), (*,24), (f,28819), (j,1593), (J,1746), ((,639), (Z,18), (H,10052), (F,7995), (&,21), (V,1597), (<,248), (r,10400), (X,14), (N,4946), (v,4131), (l,22353), (:,1), (D,6182), (',3804), (s,52643), (e,10431), (Q,1045), (/,2), (G,6079), (M,9443), (7,17), (5,35), (w,44981), (a,63748), (_,1), (O,9293), (i,32292), (y,21879), (A,21088), (u,7667), (#,3), (I,29875), (},2), (o,34201), (k,5789), (9,28), (3,59), (],7), (K,3629), (q,1332), (-,52), (?,2), (S,13062), (C,11071), (E,8266), (Y,3976), (U,1503), (1,458), (g,14703), ([,2073), (W,14616), (m,46233), (c,23496))
```

## C. Caching
Spark's caching pulls data into a cluster-wide in-memory cache (i.e. the data is accessible from any node in the cluster). You can easily memcache data that will be accessed repeatedly. Using the above wordCounts example:
```scala
scala> wordCounts.cache()
res0: wordCounts.type = ShuffledRDD[4] at reduceByKey at <console>:26

scala> wordCounts.count()
res1: Long = 67780

scala> wordCounts.count()
res2: Long = 67780
```

## D. Self-contained Applications
* See CountingShakespeare.scala and build.sbt
```scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object CountingShakespeare {
  def main(args: Array[String]) {
    val logFile = "./input.txt" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("Lear")).count()
    val numBs = logData.filter(line => line.contains("Gwendolyn")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    sc.stop()
  }
}
```
* Note that applications should define a main() method instead of extending scala.App.
* Unlike `spark-shell`, which initializes its own `SparkContext` with its own configuration, here we must explicitly initialize a `SparkContext` as part of the program
    - We pass the SparkContext constructor a SparkConf object which contains information about our application: `val sc = new SparkContext(conf)`

### .sbt
* Our application depends on the Spark API, so we’ll also include an sbt config file, build.sbt, which explains that Spark is a dependency.
```scala
name := "Simple Project"
version := "1.0"
scalaVersion := "2.11.7"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"
```
### Directory Layout
* For sbt to work, we’ll need to locate SimpleApp.scala and build.sbt in a typical directory structure.
```bash
# Your directory layout should look like this
$ find .
.
./build.sbt
./src
./src/main
./src/main/scala
./src/main/scala/CountingShakespeare.scala
```
### JAR
With the directory structure in place, we can create a JAR package containing the application’s code, then use the spark-submit script to run our program.
```bash
# Package a jar containing your application
$ sbt package
...
[info] Packaging {..}/{..}/target/scala-2.11/simple-project_2.11-1.0.jar

# Use spark-submit to run your application
$ YOUR_SPARK_HOME/bin/spark-submit \
  --class "SimpleApp" \
  target/scala-2.11/simple-project_2.11-1.0.jar
...
```

### FOOTNOTES
- Prevent local derby and metastoreDB: https://stackoverflow.com/questions/38377188/how-to-get-rid-of-derby-log-metastore-db-from-spark-shell
