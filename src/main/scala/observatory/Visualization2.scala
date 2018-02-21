package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Interaction.{TILE_SIZE, tileLocation, tile_index}
import observatory.Visualization.interpolateColor

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  val TILE_SIZE = 256

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {

    d00 * (1-point.x) * (1-point.y) + d10* point.x * (1-point.y) + d01*point.y*(1-point.x) + d11 * point.x * point.y
  }

  def frac(number: Double) : Double = {number - number.toInt}

  def getTemperature(loc: Location, grid: GridLocation => Temperature) : Temperature = {
    val cellPoint = CellPoint(frac(loc.lon),frac(loc.lat))
    val lat = loc.lat.toInt
    val lon = loc.lon.toInt
    val g00 = GridLocation(lat, lon)
    val g01 = GridLocation(lat+1, lon)
    val g10 = GridLocation(lat, lon+1)
    val g11 = GridLocation(lat+1, lon+1)
    bilinearInterpolation(cellPoint, grid(g00),grid(g01), grid(g10),grid(g11))
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {

    val points = for(x <- 0 until TILE_SIZE;
                     y <- 0 until TILE_SIZE)
      yield MapPoint(x,y)

    val pixels = points.par.map(p => (p, MapPoint(TILE_SIZE * tile.x + p.x, TILE_SIZE * tile.y + p.y)))
      .map( pair => (pair._1, Tile(pair._2.x, pair._2.y, tile.zoom + 8 )))
      .map( t_pair => (t_pair._1, tileLocation(t_pair._2)))
      .map(p => (p._1,getTemperature(p._2, grid)))
      .map(pt => (pt._1,interpolateColor(colors, pt._2)))
      .map(ptc => (ptc._1, Pixel(com.sksamuel.scrimage.Color(ptc._2.red, ptc._2.green, ptc._2.blue, 127))))

    assert(pixels.size == points.size)

    val pxl = new Array[Pixel](points.size)

    pixels.foreach(p => {
      val i = tile_index(p._1)
      //println(p._1 + " ->" + p._1 +" ->" + i + s" (${p._2})")
      pxl.update(i, p._2)
    })

    assert(pxl.size == TILE_SIZE * TILE_SIZE)

    Image(TILE_SIZE,TILE_SIZE,pxl)
  }

}
