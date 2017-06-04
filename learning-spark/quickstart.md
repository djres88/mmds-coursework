Working on https://spark.apache.org/docs/1.2.0/quick-start.html, w/ a twist to make sure I'm learning it.

#Part I: Spark Shell

##Intro 
By now you should have all the things installed. **TODO: Write instructions**
###Shell Setup
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


###Project Setup
We're ready to set up a project. Let's grab a big text file so that we can start playing with spark-shell! 

Following the hadoop intro assignment from a course I'm taking -- see mmds at https://lagunita.stanford.edu/courses/course-v1:ComputerScience+MMDS+Fall2016/about -- I'll use the complete works of Shakespeare. You should feel free to choose any file you want, though. Basketball play by play logs? Song lyrics? Abset strange formatting issues, any massive any text file will do. 

To get the Shakespeare file, run the following in your terminal:
```bash
`curl http://www.gutenberg.org/cache/epub/100/pg100.txt | perl -pe 's/^\xEF\xBB \xBF//' > input.txt`
```

Now we have our input file. Let's see what's in there.

ATTRIBUTION NOTE: The following is all based on / largely taken from the quickstart, with edits and clarifications where applicable.
##A. Spark & `spark-shell` Basics
To start up the spark-shell, run `spark-shell`. You should see logs, followed by a command prompt (`>scala`). Minor warnings are normal, errors are not. 

So what's here?
* Spark’s primary abstraction is a distributed collection of items called a *Resilient Distributed Dataset (RDD)*.
    - RDDs can be created from Hadoop InputFormats (such as HDFS files) or by transforming other RDDs. 
    - For example, to create a new RDD from an input file (which in our case contains the complete works of Shakespeare), you could transform the input file to an RDD as follows:
```scala
scala> val shakespeare = sc.shakespeare("input.txt")
shakespeare: spark.RDD[String] = spark.MappedRDD@2ee9b6e3
```
* What can you do with an RDD?
    - RDDs have *actions* (which return values) and *transformations* (which return pointers to new RDDs)
    - Rough analogy: You can think of actions as "properties" and transformations as "methods", if that lingo seems more familiar
    - **Actions** return values. For example
```scala
scala> shakespeare.count() // Number of items in this RDD
res0: Long = 126

scala> shakespeare.first() // First item in this RDD
res1: String = # Apache Spark
```
    - **Transformations** return pointers to new RDDs. For example
    
```scala
scala> val romeo = shakespeare.filter(line => line.contains("Romeo"))
linesWithSpark: spark.RDD[String] = spark.FilteredRDD@7dd4af09
```


FOOTNOTES
- Prevent local derby and metastoreDB: https://stackoverflow.com/questions/38377188/how-to-get-rid-of-derby-log-metastore-db-from-spark-shell