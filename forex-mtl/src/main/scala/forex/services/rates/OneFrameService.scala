package forex.services.rates

import forex.domain.Timestamp
import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.time.{Instant, ZonedDateTime}
import scala.annotation.unused

class OneFrameService(auth : String = "10dc303535874aeccc86a8251e6992f5") {

  val port = 8080
  val ROOT = s"http://localhost:$port/"
  val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", auth))

  // TODO could crate a getRates method too, however get rate is a bridge to streaming rates that will really by used
  def getRate(ccyPair: String): Rate = {
    val replyWithBrackets = HttpVerySimple.httpGet(ROOT + s"rates?pair=$ccyPair", reqProp = authReqProp) // i.e. "http://localhost:8080/rates?pair=NZDUSD"
    val reply = removeOuterBrackets(replyWithBrackets)

    case class RateApi(from: String, to: String, bid: Double, ask: Double, price: Double, time_stamp: Instant)
    implicit val blogDecoder: Decoder[RateApi] = deriveDecoder

    import io.circe.parser.parse
    val rateE: Either[ParsingFailure, Json] = parse(reply)
    if (rateE.isLeft) {
      println("reply parse failed=" + reply)
    }
    val rateApiResult = rateE.toOption.get.as[RateApi]
    // TODO handel {"error":"Forbidden"}
    if (rateApiResult.isLeft) {
      println("as RateApi failed=" + rateApiResult.left.toOption.get + " \nreply=" + reply)
    }
    val rateApi = rateApiResult.toOption.get
    val timestamp = Timestamp(rateApi.time_stamp.atOffset(ZonedDateTime.now().getOffset()))
    Rate(rateApi.from + rateApi.to, rateApi.price, timestamp)
  }

  private def removeOuterBrackets(ss: String, left: Char = '[', right: Char = ']'): String =
    if (ss.contains(left) && ss.contains(right)) ss.drop(1).dropRight(1) else ss



  def getStreamingRates(@unused ccyPairs: Seq[String]) : Unit =
  {
    // TODO
  }


}