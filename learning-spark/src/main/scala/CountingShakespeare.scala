import org.apache.spark.sql.SparkSession

object CountingShakespeare {
  def main() {
    val logFile = "./input.txt" // Should be some file on your system
    val spark = SparkSession.builder().master("local").appName("spark session").getOrCreate()
    val logData = spark.sparkContext.textFile(logFile, 2).cache()
    val countLear = logData.filter(line => line.contains("Lear")).count()
    val countCordelia = logData.filter(line => line.contains("Cordelia")).count()
    val countGoneril = logData.filter(line => line.contains("Goneril")).count()
    val countRegan = logData.filter(line => line.contains("Regan")).count()
    println(s"Lear lines: $countLear")
    println(s"Cordelia lines: $countCordelia")
    println(s"Goneril lines: $countGoneril")
    println(s"Regan lines: $countRegan")
  }
}
