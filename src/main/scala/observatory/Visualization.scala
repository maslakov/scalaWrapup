package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val p = 3
    val twd = temperatures.par.map { case (l, t) => {
      val dis = distance(l.lon, l.lat, location.lon, location.lat)
      val w = if (dis != 0) 1 / Math.pow(dis, p) else 1
      (t, w, dis)
    }
    }.toList

    val zeroItem = twd.filter(i=>i._3 == 0)
    if (zeroItem.isEmpty) {
      val top = twd.map(i => if (i._3 >= 1) i._1 * i._2 else i._1).sum
      val down = twd.map(i => i._2).sum
      top/down
    }else{
      zeroItem.head._1
    }
  }

  def radian(x: Double): Double = x * Math.PI / 180

  def distance(lon1: Double, lat1: Double, lon2: Double, lat2: Double): Double = {
    val dlon = radian(lon2 - lon1);
    val dlat = radian(lat2 - lat1);

    val a = (Math.sin(dlat / 2) * Math.sin(dlat / 2)) + Math.cos(radian(lat1)) * Math.cos(radian(lat2)) * (Math.sin(dlon / 2) * Math.sin(dlon / 2));
    val angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return angle * 6378.16;
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    // -5
    val allItems = points.toList.sortBy(p => p._1) // -60 -50 -27 -12 0 12 30 50

    val maxT = getAbove(allItems, value)
    val minT = getBelow(allItems, value)

    val n_value = Math.max(Math.min(value, maxT._1), minT._1)

    val fraction = if ((maxT._1 - minT._1) > 0) (n_value - minT._1) * 1.0 / (maxT._1 - minT._1) else 0
    //println(fraction)
    val color = interpolateColor(maxT._2, minT._2, fraction)
    color
  }

  def getAbove(sortedPoints: List[(Temperature, Color)], value: Temperature): (Temperature, Color) = {

    val maxItems = sortedPoints.filter(p => p._1 >= value) // ascending 0 12 30 50
    if (maxItems.nonEmpty) maxItems.head else sortedPoints.last
  }

  def getBelow(sortedPoints: List[(Temperature, Color)], value: Temperature): (Temperature, Color) = {

    val minItems = sortedPoints.filter(p => p._1 <= value) // ascending -50 -27 -12 0
    if (minItems.nonEmpty) minItems.last else sortedPoints.head
  }

  private def interpolateColor(colorMax: Color, colorMin: Color, f: Double): Color = {
    val red = (colorMax.red - colorMin.red) * f + colorMin.red
    val green = (colorMax.green - colorMin.green) * f + colorMin.green
    val blue = (colorMax.blue - colorMin.blue) * f + colorMin.blue
    Color(red.round.toInt, green.round.toInt, blue.round.toInt)
  }

  def indexy(lat_lon: GeoPoint): MapPoint = {
    val xOff = 180
    val yOff = 90
    val lat = lat_lon.lat
    val lon = lat_lon.lon
    val px = lon + xOff
    val py = yOff - lat
    MapPoint(px.toInt,py.toInt)
  }

  def index(xy:MapPoint): Int = {
    xy.x + 360*xy.y
  }
  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val points = for(lon <- -180 until 180;
    lat <- -89 until 91) yield GeoPoint(lat,lon)

    assert(points.size == 180*360)

    val pixels = points.par
      .map(p => (p ,predictTemperature(temperatures, Location(p.lat, p.lon))))
      .map(pt => (pt._1,interpolateColor(colors, pt._2)))
      .map(ptc => (ptc._1, Pixel(com.sksamuel.scrimage.Color(ptc._2.red, ptc._2.green, ptc._2.blue))))

    assert(pixels.size == points.size)

    val pxl = new Array[Pixel](points.size)



    pixels.foreach(p => {
      val xy = indexy(p._1)
      val i = index(xy)
      //println(p._1 + " ->" + xy +" ->" + i + s" (${p._2})")
      pxl.update(i, p._2)
    })

    assert(pxl.size == 180*360)

    Image(360,180,pxl)
  }

}

