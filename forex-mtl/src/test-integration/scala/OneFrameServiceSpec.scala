import forex.UnitSpec
import forex.domain.Ccy.CcyPairs
import forex.services.oneframe._
import forex.services.rates.interpreters.{provisionOfService, usageOfService}

import java.io.ByteArrayInputStream

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

  it should "decode streaming rates data array of json [{..}]" in
  {
    val json = """[{"from":"USD","to":"JPY","bid":0.5596033484836841,"ask":0.6937973188546951,"price":0.6267003336691896,"time_stamp":"2022-09-04T14:01:03.108Z"}]"""
    val consumer = Helper.Consumer()
    val stream = new ByteArrayInputStream(json.getBytes)
    val streamReader = new OneFrameRateStreamReader(consumer,params = ("http://localhost:8080/streaming/rates?pair=USDJPY",stream,Seq("USDJPY")))
    streamReader.start()
    Thread.sleep(200)
    assert(consumer.rateCount >= 1)
  }

  it should "decode streaming rates data array of json [{..},{..}]" in {
    val json =
      """[{"from":"USD","to":"JPY","bid":0.5596033484836841,"ask":0.6937973188546951,"price":0.6267003336691896,"time_stamp":"2022-09-04T14:01:03.108Z"},{"from":"USD","to":"JPY","bid":0.5596033484836841,"ask":0.6937973188546951,"price":0.6267003336691896,"time_stamp":"2022-09-04T14:01:03.108Z"}]""".stripMargin
    val consumer = Helper.Consumer()
    val stream = new ByteArrayInputStream(json.getBytes)
    val streamReader = new OneFrameRateStreamReader(consumer, params = ("http://localhost:8080/streaming/rates?pair=USDJPY", stream, Seq("USDJPY")))
    streamReader.start()
    Thread.sleep(200)
    assert(consumer.rateCount == 2)
  }

  it should "decode streaming rates data array of json {..}" in {
    val json = """{"from":"USD","to":"JPY","bid":0.5596033484836841,"ask":0.6937973188546951,"price":0.6267003336691896,"time_stamp":"2022-09-04T14:01:03.108Z"}"""
    val consumer = Helper.Consumer()
    val stream = new ByteArrayInputStream(json.getBytes)
    val streamReader = new OneFrameRateStreamReader(consumer, params = ("http://localhost:8080/streaming/rates?pair=USDJPY", stream, Seq("USDJPY")))
    streamReader.start()
    Thread.sleep(200)
    assert(consumer.rateCount == 1)
  }

  it should "decode streaming rates data of 'Not found'" in {
    val json = """Not Found"""
    val consumer = Helper.Consumer()
    val stream = new ByteArrayInputStream(json.getBytes)
    val streamReader = new OneFrameRateStreamReader(consumer, params = ("http://localhost:8080/streaming/rates?pair=USDJPY", stream, Seq("USDJPY")))
    streamReader.start()
    Thread.sleep(200)
    assert(consumer.rateCount == 0)
    assert(consumer.errorCount == 1)

  }

  it should "get a stream of rates from OneFrame" in
  {
    val consumer = Helper.Consumer()
    val streamReader = new OneFrameRateStreamReader(consumer,oneFrame.getStreamingRates(Seq ("NZDJPY","USDGBP")))
    streamReader.start()

    val consumer2 = Helper.Consumer()
    val streamReader2 = new OneFrameRateStreamReader(consumer2, oneFrame.getStreamingRates(Seq("USDJPY")))
    streamReader2.start()


    Thread.sleep(10_000)

      streamReader.safeStop()
      streamReader2.safeStop()

      assert (streamReader.lineCount >= 4)
    assert (consumer.rateCount >= 4)
    assert (consumer.errorCount == 0)
  }

  object Helper
  {
    case class Consumer() extends OneFrameRateConsumer {
      var rateCount = 0
      var errorCount = 0

      override def consumeRate(oneFrameRate: OneFrameRate): Unit = {
        rateCount += 1
        println(oneFrameRate)
      }

      override def consumeRateErrors(ccyPairs: CcyPairs, throwable: Throwable): Unit = {
        errorCount += 1
        println(s"$ccyPairs ${throwable.getMessage}")
        throwable.printStackTrace()

      }
    }
  }
}
