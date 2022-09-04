import forex.UnitSpec
import forex.http.HttpVerySimple
import forex.services.oneframe.OneFrameService
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}
import org.http4s.blaze.http.Url

import java.time.OffsetDateTime

class StaleRateSpec extends  UnitSpec {
  case class RateApi(from: String, to: String, price: Double, timestamp: OffsetDateTime)

  implicit val rateApiDecoder: Decoder[RateApi] = deriveDecoder

  "A running forex service" should """return {"error":"Stale rate:XXX"} when rate is stale"""

  println("assumes both OneFrame service  AND  forex service is running ...")
  println("fetching rate for JPY,NZD...")

  val port = 8081
  val ROOT = s"http://localhost:$port/"
  val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", "10dc303535874aeccc86a8251e6992f5")) // TODO API id

  val url:Url = ROOT + "rates?from=NZD&to=USD"
  val reply = HttpVerySimple.httpGet(url, reqProp = authReqProp)
  reply should startWith("""{"from":"NZD","to":"USD","price"""")

  val rateE: Either[ParsingFailure, Json] = io.circe.parser.parse(reply)
  assert(rateE.isRight, s"${rateE.left} jsonStr=$reply")
  val rateO = rateE.toOption.get.as[RateApi]
  assert(rateO.isRight, s"${rateO.left} jsonStr=$reply")
  val rate = rateO.toOption.get

  rate.from should be("NZD")
  rate.to should be("USD")
  rate.price should not be 100 // was hard coded price to start with

  println("rate ="+rate)
  println(s"shutdown OneFrame service to test stale rate ${OneFrameService.rateStaleDuration} after shutdown")
  val sleep = 1_000 * 30
  val n =12  // * 30 seconds
  for(i <- 1 to n)
    {
      val reply = HttpVerySimple.httpGet(url, reqProp = authReqProp)
      val rateE: Either[ParsingFailure, Json] = io.circe.parser.parse(reply)
      assert(rateE.isRight, s"${rateE.left} jsonStr=$reply")
      val rateO = rateE.toOption.get.as[RateApi]
      assert(rateO.isRight, s"${rateO.left} jsonStr=$reply")
      val rateNew = rateO.toOption.get

      print(s"current rate="+rateNew)
      print(s"  sleep $i 30s of $n ....")
      println(s"shutdown OneFrame service to test stale rate ${OneFrameService.rateStaleDuration} after shutdown")

      Thread.sleep(1_000 * 30)

    }

  println("finished")



}
