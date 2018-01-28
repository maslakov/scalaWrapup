import observatory.{Color, Location, Temperature, Visualization}

val sortedPoints: List[(Temperature, Color)] = observatory.referenceColors.sortBy(p=>p._1).toList



Visualization.interpolateColor(observatory.referenceColors, -5.0)
Visualization.interpolateColor(observatory.referenceColors, +5.0)
Visualization.interpolateColor(observatory.referenceColors, +6.0)

val max: (Temperature, Color) = Visualization.getAbove(sortedPoints, 5.0)
val min: (Temperature, Color) = Visualization.getBelow(sortedPoints, 5.0)

Visualization.getAbove(sortedPoints, 65.0)
Visualization.getBelow(sortedPoints, 65.0)

Visualization.getAbove(sortedPoints, 60.0)
Visualization.getBelow(sortedPoints, 60.0)

Visualization.getAbove(sortedPoints, -65.0)
Visualization.getBelow(sortedPoints, -65.0)

Visualization.getAbove(sortedPoints, -60.0)
Visualization.getBelow(sortedPoints, -60.0)


Visualization.interpolateColor(observatory.referenceColors, 65.0)
Visualization.interpolateColor(observatory.referenceColors, 60.0)
Visualization.interpolateColor(observatory.referenceColors, -60.0)
Visualization.interpolateColor(observatory.referenceColors, -62.0)

Visualization.distance(52,12,52,12)
Visualization.radian(0.0)
Visualization.distance(0,0,0,180)

Math.PI* 6378.16

val data = Seq(
  (Location(10,10),25.0),
  (Location(10.2,10),23.0),
  (Location(10.5,10),24.0)
)
val predict = Visualization.predictTemperature(data,Location(10.1,10.1))

val xy1 = Visualization.indexy((68,-74))
Visualization.index(xy1)

val lat_lon = (0,0)

val xOff = 180
val yOff = 90

val lat = lat_lon._1
val lon = lat_lon._2
val px = lon + xOff
val py = yOff - lat


Visualization.indexy((0,0))


Visualization.indexy((68,-22))
Visualization.indexy((-68,-22))
Visualization.indexy((-68,22))
Visualization.indexy((68,22))
Visualization.indexy((1,67))

Visualization.index((247,89))