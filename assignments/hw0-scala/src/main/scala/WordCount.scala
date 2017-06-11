import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object WordCount {
   def main(args: Array[String]) {
     val conf = new SparkConf().setAppName("Counting Shakespeare")
     val sc = new SparkContext(conf)
     println(args);
     val tokenized = sc.textFile(args(0)).flatMap(_.split(" ").map(word => word.toLowerCase))
     val wordCounts = tokenized.map((_, 1)).reduceByKey(_ + _)
     val moreThan100 = wordCounts
      .filter(_._2 >= 100)
      .collect()
      .sortWith(_._2 > _._2)
      .mkString("\n ") //_2 refers to the count , _1 refers to the key (word)

     System.out.println(s"All the words: $moreThan100")
   }
}
