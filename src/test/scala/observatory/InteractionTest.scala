package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

trait InteractionTest extends FunSuite with Checkers {

  test("three zooms are present"){
    val data = List((1900,2),(1900,3),(1900,4),(1900,1))
    var result = List[(Year, (Int, Int, Int))]()
    def returner(y: Year, t: Tile, d: Int) : Unit = {
      result = (y,(t.x,t.y,t.zoom)) :: result
    }
    Interaction.generateTiles(data, returner)

    assert(result.forall(i => i._1 == 1900))
    assert(result.forall(i => i._2._3 >= 0 && i._2._3 <=3))
    assert(result.forall(i => i._2._1 >= 0 && i._2._1 <=7))
    assert(result.forall(i => i._2._2 >= 0 && i._2._2 <=7))
  }

}
