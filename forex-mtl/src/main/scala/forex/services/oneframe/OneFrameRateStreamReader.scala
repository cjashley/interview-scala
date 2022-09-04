package forex.services.oneframe

import forex.domain.Ccy.CcyPairs
import forex.services.rates.interpreters.provisionOfService
import org.http4s.blaze.http.Url

import java.io.InputStream
import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

class OneFrameRateStreamReader(consumer: OneFrameRateConsumer, params:(Url, InputStream, CcyPairs)) extends Thread
{
  final val url:Url = params._1
  final val stream: InputStream = params._2
  final val ccyPairs: CcyPairs = params._3

  var lineCount = 0
  private var isRunning= false

  OneFrameRateStreamReader.readerCount += 1
  setName(s"stream${OneFrameRateStreamReader.readerCount}rates")


  def safeStop(): Unit = {
    isRunning = false
  }

  override def run(): Unit = {
    isRunning = true

    lineCount = 0
    val buffered = scala.io.Source.createBufferedSource(stream)
    try {
      while (buffered.hasNext && isRunning)
      {
        val startChar = buffered.next()
        val endChar: Option[Char] = startChar match {
          case '{' => Some('}')
          case '[' => Some(']')
          case _   => None
        }

        var line = new mutable.StringBuilder(startChar.toString)

        endChar match {
          case Some(c) =>
            breakable {
              while (buffered.hasNext) {
                line.append(buffered.next())
                if (line.last == c) break()
              }
            }

          case None => while (buffered.hasNext) line.append(buffered.next())
        }

            if (line.length < 4 || line.charAt(0) != '[' || line.charAt(1) != '{') // Do we have an json array to deal with?
            {
              consume(line.toString())
            }
            else
            {
//              import io.circe.parser.parse
//              consume(line.toString())
//              else
//              {
//                val s = line.toString()
//                val p = parse(s)   // TODO thread dies here, cannot step pass or into parse(s)
//                p match {
//                  case Left(e) => throw provisionOfService.ErrorInProvisionOfService(s"invalid json array ${e.getMessage()}")
//                  case Right(jArray) => for (json <- jArray.asArray) consume(json.mkString)
//                }
//              }

// rewrite of above
              if (line.charAt(line.length() - 1) != ']' && line.charAt(line.length() - 2) == '}') // Do we have an json array to deal with?
                throw provisionOfService.ErrorInProvisionOfService(s"invalid json array: ${line.toString()}")
              // we have a string [{....}] or [{...},{....}]
              line = line.drop(1).dropRight(1)

              // we have a string {....} or {...},{....}
              if (line.indexOf("},{") == -1)
                consume(line.toString()) // we have {...}
              else
                {
                // we have a string  {...},{....}
                for (segment: String <- line.toString().split("""\}\,\{"""))
                  {
                    if(segment.startsWith("{")) consume(segment+"}")
                    else if(segment.endsWith("}")) consume("{"+segment)
                    else provisionOfService.ErrorInProvisionOfService(s"invalid },{ json array: ${line.toString()}")
                }
              }
            }
      }
    }
    catch
    {
      case e:Throwable =>
        isRunning = false
        consumer.consumeRateErrors(ccyPairs,e) // upto the consumer to log

    } finally buffered.close() // end connection

  }

  private def consume(line: String): Unit = {
    lineCount += 1

    val rateApi = OneFrameService.toRateApi(url, line)
    println(getName + s"[$lineCount]=$line" )
    consumer.consumeRate(OneFrameService.asOneFrameRate(rateApi))
  }
}

object OneFrameRateStreamReader
{
  var readerCount = 0
}
