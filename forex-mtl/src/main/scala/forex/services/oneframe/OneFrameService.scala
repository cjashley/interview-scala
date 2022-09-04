  package forex.services.oneframe

import forex.domain.Ccy.{CcyPair, CcyPairs}
import forex.domain.Timestamp
import forex.http.HttpVerySimple
import forex.services.rates.interpreters.{provisionOfService, usageOfService}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.io.InputStream
import java.lang.System.Logger
import java.lang.System.Logger.Level
import java.time.{Instant, ZonedDateTime}

  class OneFrameService(auth : String = "10dc303535874aeccc86a8251e6992f5") {
    private final val log: Logger = System.getLogger(this.getClass.getName)

    val port = 8080
    val ROOT = s"http://localhost:$port"
    val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", auth))

    case class OneFrameApiRate(from: String, to: String, bid: Double, ask: Double, price: Double, time_stamp: Instant)

    implicit val rateDecoder: Decoder[OneFrameApiRate] = deriveDecoder


    import io.circe.parser.parse

    def toRateApi(url: String, jsonStr: String): OneFrameApiRate = {
      case class OneFrameApiError(error: String)
      implicit val errorDecoder: Decoder[OneFrameApiError] = deriveDecoder

      if (jsonStr.startsWith("""Not found""")) throw provisionOfService.ErrorInProvisionOfService("Not found: " + url)

      val jsonE: Either[ParsingFailure, Json] = parse(jsonStr)
      val json = jsonE.getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: " + jsonStr))

      if (jsonStr.startsWith("""{"error":""")) {
        val errorApi = json.as[OneFrameApiError].getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: " + jsonStr))
        errorApi.error match {
          case "Invalid Currency Pair" => throw usageOfService.ErrorWithCurrencyPairGiven(s"$jsonStr with url $url");
          case "No currency pair provided" => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr with url $url"); // users don't call directly, therefore its a provisioning error
          case "Quota reached" => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr said to be 1,0000");
          case "Forbidden" => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr likely invalid auth token");
          case _ => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr is not coded for in service");
        }
      }

      json.as[OneFrameApiRate].getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: " + jsonStr))
    }

    // TODO could crate a getRates method too, however get rate is a bridge to streaming rates that will really by used
    def getRate(ccyPair: CcyPair): OneFrameRate = {
      val url = s"$ROOT/rates?pair=$ccyPair"
      val replyWithBrackets = HttpVerySimple.httpGet(url, reqProp = authReqProp) // i.e. "http://localhost:8080/rates?pair=NZDUSD"
      val reply = removeOuterBrackets(replyWithBrackets)
      val rateApi = toRateApi(url, reply)
      val timestamp = Timestamp(rateApi.time_stamp.atOffset(ZonedDateTime.now().getOffset))
      OneFrameRate(rateApi.from + rateApi.to, rateApi.price, timestamp)
    }


    private def removeOuterBrackets(ss: String, left: Char = '[', right: Char = ']'): String = {
      if (ss.contains(left) && ss.contains(right)) ss.drop(1).dropRight(1) else ss
    }

    def getStreamingRates(ccyPairs: CcyPairs): InputStream = {

      log.log(Level.TRACE,"getStreamingRate:" + ccyPairs)

      def makeHttpParams(parmName: String, ccyPairs: CcyPairs): String = {
        val params = for (elem <- ccyPairs) yield {
          parmName + elem
        }
        params.mkString("&")
      }

      val ccyPairsParams = makeHttpParams("pair=", ccyPairs)
      val url = s"$ROOT/streaming/rates?$ccyPairsParams}"

      HttpVerySimple.httpGetStream(url, reqProp = authReqProp, readTimeout = 2000) // i.e. "http://localhost:8080/streaming/rates?pair=NZDUSD"
    }

  }


