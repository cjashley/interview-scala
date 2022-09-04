package forex.services.oneframe

import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

class OneFrameRateStreamReader(stream: java.io.InputStream) extends Thread
  {
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

        val line = new mutable.StringBuilder(startChar.toString)

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
        println(getName+s"[$lineCount]=$line")
        lineCount += 1
        if (lineCount == 20) return
      }

    } finally buffered.close() // end connection

  }

}

object OneFrameRateStreamReader
{
  var readerCount = 0
}
