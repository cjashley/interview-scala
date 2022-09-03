package forex.services.rates

import forex.domain.Ccy.{CcyPair, CcyPairs}
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
  def getRate(ccyPair: CcyPair): Rate = {
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
    val timestamp = Timestamp(rateApi.time_stamp.atOffset(ZonedDateTime.now().getOffset))
    Rate(rateApi.from + rateApi.to, rateApi.price, timestamp)
  }

  private def removeOuterBrackets(ss: String, left: Char = '[', right: Char = ']'): String = {
    if (ss.contains(left) && ss.contains(right)) ss.drop(1).dropRight(1) else ss
  }

  /**
   * TODO get streaming rates
   * @param ccyPairs seq of cccyPairs to stream from OneFrame
   * @return TODO
   */
  def getStreamingRates(ccyPairs: CcyPairs) : Unit =
  {
    throw new RuntimeException("NotImplemented yet "+ccyPairs)
/*
    def streamReader(stream: java.io.InputStream): Unit = {
      val buffered = scala.io.Source.createBufferedSource(stream)

      try {

        var i = 0
        while (buffered.hasNext && i < 1000) {
          print(buffered.next())
          if (math.floorMod(i, 100) == 0) println()
          i += 1
        }

      }

  val ccyPairsParams = ""

      @unused
      val replyWithBrackets = HttpVerySimple.httpGet(ROOT + s"rates?$ccyPairsParams", reqProp = authReqProp) // i.e. "http://localhost:8080/rates?pair=NZDUSD"

      HttpVerySimple.httpGetStream(ROOT + "streaming/rates?pair=NZDUSD", streamReader, reqProp = authReqProp, readTimeout = 2000)
*/
    }

  @unused
  private def makeHttpParams(parmName: String, ccyPairs: CcyPairs ) =
    {
      parmName + ccyPairs.toString() // TODO construct list
    }


}