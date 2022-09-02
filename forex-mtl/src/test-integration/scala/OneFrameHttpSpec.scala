import forex.UnitSpec
import forex.http.HttpVerySimple

/**
 * Test the replies from the OneFrame server running on port 8080
 */
 class OneFrameHttpSpec extends UnitSpec {

 "A HttpVerySimple " should "fetch data from REST services"

  val port = 8080
  val ROOT = s"http://localhost:$port/"
  val authReqProp: Seq[(String, String)] = Seq(Tuple2("token", "10dc303535874aeccc86a8251e6992f5"))


  it should "get rates from OneFrame should retrieve one rate if authorized" in
  {

    val replyNoAuth = HttpVerySimple.httpGet(ROOT + "rates?pair=NZDUSD") // i.e. "http://localhost:8080/rates?pair=NZDUSD"

//    replyNoAuth should be("{\"error\":\"Forbidden\"}")
    replyNoAuth should be("""{"error":"Forbidden"}""")

    val reply = HttpVerySimple.httpGet(ROOT + "rates?pair=NZDUSD",reqProp = authReqProp)

//    reply should startWith("[{\"from\":\"NZD\",\"to\":\"USD\",\"bid\"")
    reply should startWith("""[{"from":"NZD","to":"USD","bid"""")

  }


  it should "fetch streaming get from OneFrame should retrieve a stream of rates if authorized" in
  {
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

      finally
        buffered.close() // end connection
    }

    HttpVerySimple.httpGetStream(ROOT + "streaming/rates?pair=NZDUSD", streamReader, reqProp = authReqProp, readTimeout = 2000)
  }
}
