package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


trait ExtractionTest extends FunSuite {

  test("Read two stations"){
    val data = Extraction.locateTemperatures(2016,"/stations1.csv","/r1.csv")
    assert(data.size == 6)
  }

  test("Average two stations"){
    val data = Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(2016,"/stations1.csv","/r1.csv"))
    assert(data.size == 2)
    assert(data.forall(r=>r._2 < 0.0000001)) // 32F == 0C
  }


}