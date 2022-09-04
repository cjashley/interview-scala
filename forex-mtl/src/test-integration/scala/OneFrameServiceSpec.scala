import forex.UnitSpec
import forex.services.oneframe._
import forex.services.rates.interpreters.{provisionOfService, usageOfService}

class OneFrameServiceSpec extends UnitSpec {

  "A OneFrameService " should "fetch request from a running OneFrame server"
  val oneFrame = new OneFrameService()

  it should "get a rate from OneFrame" in
  {
    val rate = oneFrame.getRate("JPYNZD")

    rate.ccyPair should be("JPYNZD")
  }

  it should "throw ErrorWithCurrencyPairGiven if invalid currency" in {
    intercept[usageOfService.ErrorWithCurrencyPairGiven] { oneFrame.getRate("XXXYYY")}
  }


  it should "throw provisionOfService.ErrorInProvisionOfService with bad auth code" in
  {
      val oneFrameX = new OneFrameService(auth = "12121BadAuthCode1231")
      intercept[provisionOfService.ErrorInProvisionOfService] {oneFrameX.getRate("GBPNZD")}
  }

  it should "get a stream of rates from OneFrame" taggedAs NotImplementedYet in
  {
    val streamReader = new OneFrameRateStreamReader(oneFrame.getStreamingRates(Seq ("NZDJPY","USDGBP")))
    streamReader.start()

    {
      val streamReader2 = new OneFrameRateStreamReader(oneFrame.getStreamingRates(Seq("USDJPY")))
      streamReader2.start()
    }

    Thread.sleep(5000)
    streamReader.safeStop()
    assert (streamReader.lineCount >= 4)
  }

}
