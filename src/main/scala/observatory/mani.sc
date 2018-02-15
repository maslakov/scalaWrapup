import observatory.{GridLocation, Location, Manipulation, Temperature}

val data = for(lon <- -180 until 180;
               lat <- -89 until 91)
  yield (Location(lat,lon),lat+lon :Temperature )

val fn: (GridLocation) => Temperature = Manipulation.makeGrid(data)
val gl = GridLocation(0,0)
fn(gl)
fn GridLocation(1,1)