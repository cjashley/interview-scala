import forex.UnitSpec
import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.time.OffsetDateTime

/**
 * Test the replies from the Forex server running on port 8081
 */
final class ForexHttpSpec extends UnitSpec {

  "A Forex Http endpoint" should "fetch serve responses to test"

  val port = 8081
  val ROOT = s"http://localhost:$port/"
  val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", "10dc303535874aeccc86a8251e6992f5")) // TODO API id

  // rates get from OneFrame should retrieve one rate if authorized
  {
    val reply = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)

    reply should startWith("""{"from":"NZD","to":"USD","price"""")
  }

  it should "reply with an error when request has no API token"  taggedAs NotImplementedYet in
    {

      val replyNoAuth = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD") // i.e. "http://localhost:8080/rates?pair=NZDUSD"

      replyNoAuth should be("{\"error\":\"Forbidden no Auth token\"}")
    }

  // get one rate and then a second rate for the same currency pair, timestamp should be different, hopefully this indicates price will be too
  it should "be updating rates with new prices" taggedAs NotImplementedYet in
    {

      val reply1 = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)
      val reply2 = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)

      case class RateApi(from: String, to:String, price: Int, timestamp: OffsetDateTime)
      implicit val blogDecoder: Decoder[RateApi] = deriveDecoder

      import io.circe.parser.parse
      val rate1E:Either[ParsingFailure,Json] = parse(reply1)
      val rate2E:Either[ParsingFailure,Json] = parse(reply2)

      val rate1 = rate1E.toOption.get.as[RateApi].toOption.get
      val rate2 = rate2E.toOption.get.as[RateApi].toOption.get

      rate1.timestamp should not be rate2.timestamp
    }

  it should "return invalid currency when given from XXX" taggedAs NotImplementedYet in
  {
    // At the moment (until implemented), returns Server returned HTTP response code: 500 for URL: http://localhost:8081/rates?from=XXX&to=USD
    val reply1 = HttpVerySimple.httpGet(ROOT + "rates?from=XXX&to=USD", reqProp = authReqProp)

    reply1 should be("""{"error":"Invalid Currency XXX"}""")
  }
}
