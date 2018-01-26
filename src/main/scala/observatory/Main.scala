package observatory

object Main extends App {

  override def main(args: Array[String]) {

    val file = args.head
    val filename = s"/$file.csv"
    println(filename)
    println(this.getClass.getResource(filename).getPath)

    val data = Extraction.locationYearlyAverageRecords(
      Extraction.locateTemperatures(file.toInt,"/stations.csv", filename ))

    val image = Visualization.visualize(data, observatory.referenceColors)

    image.output(new java.io.File(s"target/$file.png"))
  }

}
