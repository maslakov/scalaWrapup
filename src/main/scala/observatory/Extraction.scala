package observatory

import java.time.LocalDate

import observatory.SparkContextKeeper.sc
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

import scala.collection.JavaConverters._


object SparkContextKeeper {
  val conf = new SparkConf().setMaster("local[2]").setAppName("my app")
  val sc = new SparkContext(conf)
  val sparkSession = SparkSession.builder
    .config(conf = conf)
    .appName("spark session example")
    .getOrCreate()


}

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {

    import SparkContextKeeper.sparkSession.implicits._

    val stationSchema = StructType(Array(
      StructField("stn", StringType, true),
      StructField("wban", StringType, true),
      StructField("lat", DoubleType, true),
      StructField("lon", DoubleType, true)))

    val stationsDf = SparkContextKeeper.sparkSession.read
      .format("csv")
      .option("header", "false") //reading the headers
      .option("mode", "DROPMALFORMED")
      .schema(stationSchema)
      .load(this.getClass.getResource(stationsFile).getPath)

    val stationsDs = stationsDf.filter($"lat".isNotNull && $"lon".isNotNull).as[Station]

    val tempSchema = StructType(Array(
      StructField("stn", StringType, true),
      StructField("wban", StringType, true),
      StructField("mon", IntegerType, true),
      StructField("day", IntegerType, true),
      StructField("tf", DoubleType, true)))


    val tempDf = SparkContextKeeper.sparkSession.read
      .format("csv")
      .option("header", "false") //reading the headers
      .option("mode", "DROPMALFORMED")
      .schema(tempSchema)
      .load(this.getClass.getResource(temperaturesFile).getPath)

    val tempDs = tempDf.filter($"mon".isNotNull && $"day".isNotNull && $"tf".isNotNull).as[Record]

    val merge = stationsDs
      .joinWith(tempDs, stationsDs.col("stn") <=> tempDs.col("stn")
        && stationsDs.col("wban") <=> tempDs.col("wban"));

    //merge.show()

    merge
      .collect()
      .map(
        (sr) =>
          (LocalDate.of(year, sr._2.mon, sr._2.day), Location(sr._1.lat, sr._1.lon), fahrenheitToCelsius(sr._2.tf))
      )
  }


  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    sparkAverageRecords(sc.parallelize(records.toSeq)).collect()
  }


  def sparkAverageRecords(records: RDD[(LocalDate, Location, Temperature)]): RDD[(Location, Temperature)] = {
    records.groupBy(k => k._2).map { case (l, r) => (l, r.map(dlt => dlt._3).sum / r.size) }
  }

}
