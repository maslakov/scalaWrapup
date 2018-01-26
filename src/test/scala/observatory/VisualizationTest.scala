package observatory


import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite with Checkers {

  test("Above max temperature"){
    val res = Visualization.interpolateColor(observatory.referenceColors, 65.0);
    assertResult(Color(255,255,255))(res)
  }

  test("Above eq max temperature"){
    val res = Visualization.interpolateColor(observatory.referenceColors, 60.0);
    assertResult(Color(255,255,255))(res)
  }

  test("Below min temperature"){
    val res = Visualization.interpolateColor(observatory.referenceColors, -65.0);
    assertResult(Color(0,0,0))(res)
  }

  test("Below eq min temperature"){
    val res = Visualization.interpolateColor(observatory.referenceColors, -60.0);
    assertResult(Color(0,0,0))(res)
  }

  test("Mid temperature"){
    val res = Visualization.interpolateColor(observatory.referenceColors, 6.0);
    assertResult(Color(127,255,127))(res)
  }

  test("Predict by eq"){
    val data = Seq(
      (Location(10,10),25.0),
      (Location(20,20),15.0),
      (Location(10.5,10),5.0)
    )
    val predict = Visualization.predictTemperature(data,Location(20,20))
    assertResult(15.0)(predict)
  }

  test("Predict avg"){
    val data = Seq(
      (Location(10,10),25.0),
      (Location(10.2,10),23.0),
      (Location(10.5,10),24.0)
    )
    val predict = Visualization.predictTemperature(data,Location(10.1,10.2))
    assert(predict > 23 && predict < 25)
  }
}
