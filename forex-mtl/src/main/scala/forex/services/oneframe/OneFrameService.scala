  package forex.services.oneframe

import forex.domain.Ccy.{CcyPair, CcyPairs}
import forex.domain.Timestamp
import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.time.{Instant, ZonedDateTime}
import scala.annotation.unused

  class OneFrameService(auth : String = "10dc303535874aeccc86a8251e6992f5")
  {

    val port = 8080
    val ROOT = s"http://localhost:$port"
    val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", auth))
    case class OneFrameRateApi(from: String, to: String, bid: Double, ask: Double, price: Double, time_stamp: Instant)
    implicit val rateDecoder: Decoder[OneFrameRateApi] = deriveDecoder

    def toRateApi(jsonStr: String): OneFrameRateApi =
    {
      import io.circe.parser.parse
      val rateE: Either[ParsingFailure, Json] = parse(jsonStr)
      assert(rateE.isRight, s"${rateE.left} jsonStr=$jsonStr")

      val rateO = rateE.toOption.get.as[OneFrameRateApi]
      assert(rateO.isRight, s"${rateO.left} jsonStr=$jsonStr")

      rateO.toOption.get
    }
    // TODO could crate a getRates method too, however get rate is a bridge to streaming rates that will really by used
    def getRate(ccyPair: CcyPair): oneFrameRate =
    {
      val replyWithBrackets = HttpVerySimple.httpGet(s"$ROOT/rates?pair=$ccyPair", reqProp = authReqProp) // i.e. "http://localhost:8080/rates?pair=NZDUSD"
      val reply = removeOuterBrackets(replyWithBrackets)
      val rateApi = toRateApi(reply)
      val timestamp = Timestamp(rateApi.time_stamp.atOffset(ZonedDateTime.now().getOffset))
      oneFrameRate(rateApi.from + rateApi.to, rateApi.price, timestamp)
    }

    private def removeOuterBrackets(ss: String, left: Char = '[', right: Char = ']'): String =
    {
      if (ss.contains(left) && ss.contains(right)) ss.drop(1).dropRight(1) else ss
    }

    // Two types of exception, retryable and not

    /**
     * TODO get streaming rates
     *
     * @param ccyPairs seq of cccyPairs to stream from OneFrame
     * @return TODO
     */
     def getStreamingRates(ccyPairs: CcyPairs): Unit = {
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
            try
            {
              val s:Scanner = new Scanner(stream).useDelimiter(String.join("|", "\\[\\{", "\\}\\,\\{", "\\}\\]")) // [{ or },{ or }]

                var line = s.next();

                if (!line.isBlank()) // line will be empty in between each data sequence [{,,,}][{,,}]
                {
                  //  handling {"error":"No currency pair provided"}
                  //  handling {"error":"Quota reached"}
                  //  handling {"error":"Invalid Currency Pair"}
                  if (line.startsWith("{")) {
                    if (line.contains("Invalid Currency Pair")) throw new OneFrameCurrencyPairsException(currencyPairs);
                    if (line.contains("No currency pair provided")) throw new OneFrameException("No currency pair provided");
                  }

                  line = line.mkString("{",line,"}")

                  System.out.println(line);
                  System.out.flush();

                  // LOG.log(Level.INFO,line);

                  StringReader stringReader = new StringReader(line);
                  try (JsonReader jsonReader = Json.createReader(stringReader)) {
                    OneFrameRate rate = new OneFrameRate(jsonReader.readObject());
                    //								System.out.println("OneFrameRate "+rate); System.out.flush();
                    consumer.accept(rate);
                    consumeCount.getAndIncrement();
                  }
                }
              }






        }
        finally {
          buffered.close()
        }
      }

      val ccyPairsParams = makeHttpParams("pair=", ccyPairs)


      HttpVerySimple.httpGetStream(s"${ROOT}/streaming/rates?${ccyPairsParams}", streamReader, reqProp = authReqProp, readTimeout = 2000) // i.e. "http://localhost:8080/streaming/rates?pair=NZDUSD"
*/
    }

    @unused  def makeHttpParams(parmName: String, ccyPairs: CcyPairs ): String =
    {
      val params = for (elem <- ccyPairs) yield {parmName + elem}
      params.mkString("&")
    }
  }


