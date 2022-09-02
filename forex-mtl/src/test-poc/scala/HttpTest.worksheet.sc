

import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}
import io.circe.parser.parse

import java.time.OffsetDateTime

case class RateApi(from: String, to: String, price: Int , timestamp: OffsetDateTime)
implicit val blogDecoder: Decoder[RateApi] = deriveDecoder

val rateAipE:Either[ParsingFailure,Json] = parse(
"""{"from":"NZD","to":"USD","price":100,"timestamp":"2022-08-31T13:38:41.477096+09:00"}""".stripMargin)

val rateE = rateAipE.toOption.get.as[RateApi]
val rate = rateE.toOption.get
rate.timestamp
//val rate1E:Either[ParsingFailure,RateApi] = parse(rateAipE)


val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", "10dc303535874aeccc86a8251e6992f5"))

val ROOT = "http://localhost:8081/"
val reply1 = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)
val reply2 = HttpVerySimple.httpGet(ROOT + "rates?from=NZD&to=USD", reqProp = authReqProp)

val rateAipE:Either[ParsingFailure,Json] = parse(reply1)
val rateE = rateAipE.toOption.get.as[RateApi]
val rate = rateE.toOption.get
rate.timestamp



import io.circe.parser.parse
val p = parse(reply1)

val c = p.toOption.get.hcursor
c.keys
println(c.keys.toList)
c.downField("from").as[String]

val l = List("from","to")
  l.contains("to")


val rate1E: Either[ParsingFailure, Json] = parse(reply1)
val rate2E: Either[ParsingFailure, Json] = parse(reply2)

println(rate1E.left.toOption.get)

val rateAip:Either[ParsingFailure,Json] = parse(
  """{"from":"NZD","to":"USD","price":100,"timestamp":"2022-08-31T13:38:41.477096+09:00"}""".stripMargin)
private val xxx = rateAip.toOption.get.as[RateApi]




import cats.effect.{Blocker, ExitCode, IO, IOApp}
import org.http4s.client.{Client, JavaNetClientBuilder}


import java.util.concurrent.Executors
val blockingPool = Executors.newFixedThreadPool(5)
val blocker = Blocker.liftExecutorService(blockingPool)
//val httpClient: Client[IO] = JavaNetClientBuilder[IO](blocker).create


