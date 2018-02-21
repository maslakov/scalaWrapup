import observatory.Temperature

package object observatory {
  type Temperature = Double // °C, introduced in Week 1
  type Year = Int // Calendar year, introduced in Week 1

  def fahrenheitToCelsius(f: Temperature) : Temperature = (f - 32) * 5 / 9

  val referenceColors: Seq[(Temperature, Color)] = List(
    (60.0,Color(255,255,255)),
    (32.0,Color(255,0,0)),
    (12.0,Color(255,255,0)),
    (0.0,Color(0,255,255)),
    (-15.0,Color(0,0,255)),
    (-27.0,Color(255,0,255)),
    (-50.0,Color(33,0,107)),
    (-60.0,Color(0,0,0))
  )

  val gridColors: Seq[(Temperature, Color)] = List(
    (7.0,Color(0,0,0)),
    (4.0,Color(255,0,0)),
    (2.0,Color(255,255,0)),
    (0.0,Color(255,255,255)),
    (-2.0,Color(0,255,255)),
    (-7.0,Color(0,0,255))
  )

}
