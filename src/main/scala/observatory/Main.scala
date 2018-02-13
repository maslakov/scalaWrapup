package observatory

import java.nio.file.{Files, Paths}

object Main extends App {

  override def main(args: Array[String]) {

    val file = args.head
    val filename = s"/$file.csv"
    println(filename)
    println(this.getClass.getResource(filename).getPath)

    val data = Extraction.locationYearlyAverageRecords(
      Extraction.locateTemperatures(file.toInt,"/stations.csv", filename ))

    Interaction.generateTiles(List((file.toInt,data)),Interaction.makeImage)
   /*
    val zoom = 1
    val tiles = for(x <- 0 until zoom+1;
        y <- 0 until zoom+1)
      yield Tile(x,y,zoom)

    val path = Paths.get(s"target/temperatures/$file/$zoom/")
    Files.createDirectories(path)

    tiles.foreach(tile =>{
      val image = Interaction.tile(data, observatory.referenceColors, tile)
      image.output(new java.io.File(s"target/temperatures/$file/$zoom/${tile.x}-${tile.y}.png"))
    })
    */
    //val image = Visualization.visualize(data, observatory.referenceColors)
  }

}
