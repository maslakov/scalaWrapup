package observatory

import observatory.Visualization.predictTemperature

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val points = for(lon <- -180 until 180;
                     lat <- -89 until 91) yield GridLocation(lat,lon)
    val temps = points.par
      .map(p => p -> predictTemperature(temperatures, Location(p.lat, p.lon)))
      .toMap

    (gl : GridLocation) => temps(gl)
  }

  def avg(s: Iterable[Temperature]): Double = {
    val t = s.foldLeft((0.0, 0)) ((acc, i) => (acc._1 + i, acc._2 + 1));
    t._1 / t._2
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val years = temperaturess.map( year => makeGrid(year))

    (gl : GridLocation) =>{
      avg(years.map( fn => fn(gl)))
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    ???
  }


}

