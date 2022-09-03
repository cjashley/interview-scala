import forex.UnitSpec
import forex.services.rates.OneFrameService

class OneFrameServiceSpec extends UnitSpec {

  "A OneFrameService " should "fetch request from a running OneFrame server"

  it should "get a rate from OneFrame" in
  {
    val oneFrame = new OneFrameService()

    val rate = oneFrame.getRate("JPYNZD")

    rate.ccyPair should be("JPYNZD")

  }


  it should "get a stream of rates from OneFrame" taggedAs NotImplementedYet in
  {
    val oneFrame = new OneFrameService()

    oneFrame.getRates(Seq ("NZDJPY","USDGBP") )

  }

}
