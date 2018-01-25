import observatory.{Color, Temperature, Visualization}

val sortedPoints: List[(Temperature, Color)] = observatory.referenceColors.sortBy(p=>p._1).toList



Visualization.interpolateColor(observatory.referenceColors, -5.0)
Visualization.interpolateColor(observatory.referenceColors, +5.0)

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