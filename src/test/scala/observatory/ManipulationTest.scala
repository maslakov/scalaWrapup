package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait ManipulationTest extends FunSuite with Checkers {

  test("reference temperatures"){
    val data = for(lon <- -180 until 180;
                     lat <- -89 until 91)
      yield (Location(lat,lon),lat+lon :Temperature )

    val fn = Manipulation.makeGrid(data)

    val tests = for(lon <- -180 until 180;
        lat <- -89 until 91) yield GridLocation(lat,lon)

    assert(tests.forall(p=> fn(p)== p.lat+p.lon))
  }

}