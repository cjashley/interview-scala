package forex.http

import java.io.InputStream

object HttpVerySimple {

  /**
    * Returns the text (content) from a REST URL as a String.
    * Inspired by http://matthewkwong.blogspot.com/2009/09/scala-scalaiosource-fromurl-blockshangs.html
    * and http://alvinalexander.com/blog/post/java/how-open-url-read-contents-httpurl-connection-java
    *
    * The `connectTimeout` and `readTimeout` comes from the Java URLConnection
    * class Javadoc.
    *
    * param url            The full URL to connect to.
    * param connectTimeout Sets a specified timeout value, in milliseconds,
    *                       to be used when opening a communications link to the resource referenced
    *                       by this URLConnection. If the timeout expires before the connection can
    *                       be established, a java.net.SocketTimeoutException
    *                       is raised. A timeout of zero is interpreted as an infinite timeout.
    *                       Defaults to 5000 ms.
    * param readTimeout    If the timeout expires before there is data available
    *                       for read, a java.net.SocketTimeoutException is raised. A timeout of zero
    *                       is interpreted as an infinite timeout. Defaults to 5000 ms.
    * param reqProp pass in key, property pairs to be set
    * example get("http://www.example.com/getInfo")
    * example get("http://www.example.com/getInfo", 5000)
    * example get("http://www.example.com/getInfo", 5000, 5000)
    */
  import java.net.{HttpURLConnection, URL}
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def httpGet(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, reqProp: Seq[(String, String)] = Seq.empty): String = {
    val connection: HttpURLConnection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout) // TODO setting seems to be ignored
//    println("readTimeout="+ connection.getReadTimeout)
    connection.setRequestMethod("GET")
    for(prop <- reqProp) connection.setRequestProperty(prop._1,prop._2)
    val inputStream = connection.getInputStream
    val content     = scala.io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    content
  }

  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def httpGetStream(url: String, connectTimeout: Int = 5000, readTimeout: Int = 0, requestMethod: String = "GET", reqProp: Seq[(String, String)] = Seq.empty): InputStream = {
    val connection: HttpURLConnection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout) // TODO this setting seems to be ignored
    connection.setRequestMethod(requestMethod)
    for (prop <- reqProp) connection.setRequestProperty(prop._1, prop._2)
    val inputStream = connection.getInputStream
    //    val buffered = scala.io.Source.createBufferedSource(inputStream)

    // Not sure, sockettime out was not thrown with my readTimeout set
    //    } catch {
    //      case ioe: java.io.IOException => ???
    //      case ste: java.net.SocketTimeoutException => ??? // handle this
    //    }

    inputStream
  }

}
