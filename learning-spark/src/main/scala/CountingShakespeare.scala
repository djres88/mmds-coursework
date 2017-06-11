import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object CountingShakespeare {
  def main(args: Array[String]) {
    val logFile = "./input.txt" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val countLear = logData.filter(line => line.contains("Lear")).count()
    val countCordelia = logData.filter(line => line.contains("Cordelia")).count()
    val countGoneril = logData.filter(line => line.contains("Goneril")).count()
    val countRegan = logData.filter(line => line.contains("Regan")).count()
    println(s"Lear lines: $countLear")
    println(s"Cordelia lines: $countCordelia")
    println(s"Goneril lines: $countGoneril")
    println(s"Regan lines: $countRegan")
    sc.stop()
  }
}
