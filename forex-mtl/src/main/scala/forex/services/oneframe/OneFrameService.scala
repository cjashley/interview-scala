  package forex.services.oneframe

import forex.domain.Ccy.{CcyPair, CcyPairs}
import forex.domain.Timestamp
import forex.http.HttpVerySimple
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json, ParsingFailure}

import java.time.{Instant, ZonedDateTime}
import forex.services.rates.interpreters.usageOfService
import forex.services.rates.interpreters.provisionOfService

  class OneFrameService(auth : String = "10dc303535874aeccc86a8251e6992f5")
  {

    val port = 8080
    val ROOT = s"http://localhost:$port"
    val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", auth))
    case class OneFrameApiRate(from: String, to: String, bid: Double, ask: Double, price: Double, time_stamp: Instant)
    implicit val rateDecoder: Decoder[OneFrameApiRate] = deriveDecoder


    import io.circe.parser.parse
    def toRateApi(url:String, jsonStr: String): OneFrameApiRate =
    {
      case class OneFrameApiError(error: String)
      implicit val errorDecoder: Decoder[OneFrameApiError] = deriveDecoder

      if (jsonStr.startsWith("""Not found""")) throw provisionOfService.ErrorInProvisionOfService("Not found: "+url)

      val jsonE: Either[ParsingFailure, Json] = parse(jsonStr)
      val json = jsonE.getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: "+jsonStr))

      if (jsonStr.startsWith("""{"error":""")) {
        val errorApi = json.as[OneFrameApiError].getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: "+jsonStr))
        errorApi.error match {
          case "Invalid Currency Pair"      => throw usageOfService.ErrorWithCurrencyPairGiven(s"$jsonStr with url $url");
          case "No currency pair provided"  => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr with url $url"); // users don't call directly, therefore its a provisioning error
          case "Quota reached"              => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr said to be 1,0000");
          case "Forbidden"                  => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr likely invalid auth token");
          case _                            => throw provisionOfService.ErrorInProvisionOfService(s"$jsonStr is not coded for in service");
        }
      }

      json.as[OneFrameApiRate].getOrElse(throw provisionOfService.ErrorInProvisionOfService("invalid json: "+jsonStr))
    }

    // TODO could crate a getRates method too, however get rate is a bridge to streaming rates that will really by used
    def getRate(ccyPair: CcyPair): oneFrameRate =
    {
      val url = s"$ROOT/rates?pair=$ccyPair"
      val replyWithBrackets = HttpVerySimple.httpGet(url, reqProp = authReqProp) // i.e. "http://localhost:8080/rates?pair=NZDUSD"
      val reply = removeOuterBrackets(replyWithBrackets)
      val rateApi = toRateApi(url,reply)
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
  throw new RuntimeException("not imp"+ccyPairs)
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


                  line = line.mkString("{",line,"}")

                  System.out.println(line);
                  System.out.flush();

                  // LOG.log(Level.INFO,line);

//                  StringReader stringReader = new StringReader(line);
//                  try (JsonReader jsonReader = Json.createReader(stringReader)) {
//                    OneFrameRate rate = new OneFrameRate(jsonReader.readObject());
//                    //								System.out.println("OneFrameRate "+rate); System.out.flush();
//                    consumer.accept(rate);
//                    consumeCount.getAndIncrement();
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

       @unused  def makeHttpParams(parmName: String, ccyPairs: CcyPairs ): String =
       {
         val params = for (elem <- ccyPairs) yield {parmName + elem}
         params.mkString("&")
       }

   */
     }

  }


