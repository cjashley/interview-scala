import forex.UnitSpec
import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.time.{Instant, OffsetDateTime}

/**
 * Test the replies from the Forex server running on port 8081
 */
final class ForexHttpSpec extends UnitSpec {

  "A Forex Http endpoint" should "fetch serve responses to test"

  val port = 8081
  val ROOT = s"http://localhost:$port/"
  val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", "10dc303535874aeccc86a8251e6992f5")) // TODO API id

  it should "get a rate from OneFrame if authorized" in
  {
    val reply = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)
    reply should startWith("""{"from":"NZD","to":"USD","price"""")

    val rate = toRateApi(reply)
    rate.from should be ("NZD")
    rate.to should be ("USD")
    rate.price should not be 100 // was hard coded price to start with
  }

  it should "reply with an error when request has no API token"  taggedAs NotImplementedYet in
    {

      val replyNoAuth = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD") // i.e. "http://localhost:8080/rates?pair=NZDUSD"

      replyNoAuth should be("{\"error\":\"Forbidden no Auth token\"}")
    }

  // get one rate and then a second rate for the same currency pair, timestamp should be different, hopefully this indicates price will be too
  it should "be updating rates with new prices" taggedAs NotImplementedYet in
    {
      val reply1 = HttpVerySimple.httpGet(ROOT + "rates?from=JPY&to=USD", reqProp = authReqProp)
      Console println Instant.now
      Thread.sleep(2000L)
      Console println Instant.now
      val reply2 = HttpVerySimple.httpGet(ROOT + "rates?from=JPY&to=USD", reqProp = authReqProp)


      val rate1 = toRateApi(reply1)
      val rate2 = toRateApi(reply2)

      rate1.timestamp should not be rate2.timestamp
    }

  case class RateApi(from: String, to:String, price: Double, timestamp: OffsetDateTime)
  implicit val blogDecoder: Decoder[RateApi] = deriveDecoder

  def toRateApi(jsonStr:String): RateApi =
  {
    import io.circe.parser.parse
    val rateE: Either[ParsingFailure, Json] = parse(jsonStr)
    assert(rateE.isRight,s"${rateE.left} jsonStr=$jsonStr")
    val rateO = rateE.toOption.get.as[RateApi]
    assert(rateO.isRight, s"${rateO.left} jsonStr=$jsonStr")
    rateO.toOption.get
  }

  it should "return invalid currency when given from XXX" taggedAs NotImplementedYet in
  {
    // At the moment (until implemented), returns Server returned HTTP response code: 500 for URL: http://localhost:8081/rates?from=XXX&to=USD
    val reply1 = HttpVerySimple.httpGet(ROOT + "rates?from=XXX&to=USD", reqProp = authReqProp)

    reply1 should be("""{"error":"Invalid Currency XXX"}""")
  }
}
