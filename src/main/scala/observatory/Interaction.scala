package observatory

import java.nio.file.{Files, Paths}

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Visualization.{index, indexy, interpolateColor, predictTemperature}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val n = Math.pow( 2.0 , tile.zoom  )
    val lon_deg = tile.x / n * 360.0 - 180.0
    val lat_rad = math.atan(math.sinh(Math.PI * (1 - 2 * tile.y / n)))
    val lat_deg = Math.toDegrees(lat_rad)
    return Location(lat_deg, lon_deg)
  }

  val TILE_SIZE = 256

  def tile_index(xy:MapPoint): Int = {
    xy.x + TILE_SIZE*xy.y
  }
  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)],
           colors: Iterable[(Temperature, Color)],
           tile: Tile): Image = {
    val points = for(x <- 0 until TILE_SIZE;
                     y <- 0 until TILE_SIZE)
      yield MapPoint(x,y)

    val pixels = points.par.map(p => (p, MapPoint(TILE_SIZE * tile.x + p.x, TILE_SIZE * tile.y + p.y)))
    .map( pair => (pair._1, Tile(pair._2.x, pair._2.y, tile.zoom + 8 )))
    .map( t_pair => (t_pair._1, tileLocation(t_pair._2)))
    .map(p => (p._1 ,predictTemperature(temperatures, p._2)))
    .map(pt => (pt._1,interpolateColor(colors, pt._2)))
    .map(ptc => (ptc._1, Pixel(com.sksamuel.scrimage.Color(ptc._2.red, ptc._2.green, ptc._2.blue, 127))))

    assert(pixels.size == points.size)

    val pxl = new Array[Pixel](points.size)

    pixels.foreach(p => {
      val i = tile_index(p._1)
      println(p._1 + " ->" + p._1 +" ->" + i + s" (${p._2})")
      pxl.update(i, p._2)
    })

    assert(pxl.size == TILE_SIZE * TILE_SIZE)

    Image(TILE_SIZE,TILE_SIZE,pxl)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    yearlyData.par.foreach(yd =>{
      val tiles = for(zoom <- 0 until 4;
          x <- 0 until zoom+1;
          y <- 0 until zoom+1)
        yield Tile(x, y, zoom)

      tiles.foreach(tile =>{
        generateImage(yd._1, tile, yd._2)
      })

    } )
  }

  def makeImage(y:Year, tile: Tile, data: Iterable[(Location, Temperature)]) : Unit = {
    val path = Paths.get(s"target/temperatures/$y/${tile.zoom}/")
    Files.createDirectories(path)
    val image = Interaction.tile(data, observatory.referenceColors, tile)
    image.output(new java.io.File(s"target/temperatures/$y/${tile.zoom}/${tile.x}-${tile.y}.png"))
  }

}
