package object observatory {
  type Temperature = Double // °C, introduced in Week 1
  type Year = Int // Calendar year, introduced in Week 1

  def fahrenheitToCelsius(f: Temperature) : Temperature = (f - 32) * 5 / 9
}
