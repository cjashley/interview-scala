package forex.services.oneframe

import forex.domain.Ccy._
import forex.domain.Timestamp

import java.lang.System.Logger
import java.lang.System.Logger.Level
import java.time.Instant

class OneFrameRates
  {
  private final val log: Logger = System.getLogger(this.getClass.getName)
  val oneFrame = new OneFrameService() // TODO rates need refreshing from streams
  val ratesStore = new RatesStore()

  case class RateWrapper(protected var rate: OneFrameRate,
                         protected var readTimestamp: Instant,
                         protected var lastException: Option[Throwable],
                         protected var currentException: Option[Throwable]) {

    def getRate: OneFrameRate = {
      readTimestamp = Instant.now
      rate
    }

    def getReadTimestamp: Instant = readTimestamp

    def setRate(rate: OneFrameRate): Unit = {
      if (currentException.isDefined) {
        lastException = currentException
        currentException = Option.empty
      }
      this.rate = rate
    }

    def getLastException: Option[Throwable] = lastException //  last exception have been connected with this currency pair

    def setCurrentException(exception: Throwable): Unit = {
      if (currentException.isDefined) lastException = currentException
      this.currentException = Option(exception)
    }

    def setReadTimestampToEPOC(): Unit = {
      readTimestamp = Instant.EPOCH // i.e. expire rate reading
    }
  }

  object RateWrapper{

    def apply(rate: OneFrameRate):RateWrapper = {
      new RateWrapper(rate, Instant.now(), Option.empty, Option.empty)
    }
  }

  final class RatesStore extends OneFrameRateConsumer {

    private val rates = new scala.collection.mutable.HashMap[CcyPair, RateWrapper]()

    def add(oneFrameRate: OneFrameRate):RateWrapper =
    {
      val rateWrapper = RateWrapper(oneFrameRate)
      rates += (oneFrameRate.ccyPair -> rateWrapper)

      rateWrapper
    }

    def getRate(ccyPair: CcyPair): Option[RateWrapper] = rates.get(ccyPair)

    override def consumeRate(oneFrameRate: OneFrameRate): Unit =
    {
//      rates.get(oneFrameRate.ccyPair).getOrElse(rates.addOne(oneFrameRate.ccyPair -> RateWrapper(oneFrameRate))) // TODO understand why compiler warns with: discarded non-Unit value

      rates.get(oneFrameRate.ccyPair) match
      {
        case Some(wr) => wr.setRate(oneFrameRate)
        case None => rates += (oneFrameRate.ccyPair -> RateWrapper(oneFrameRate))
      }

    }

    override def consumeRateErrors(ccyPairs: CcyPairs, throwable: Throwable): Unit =
    {
      var nonReportedPairs: Seq[CcyPair] = Seq()
      for(ccyPair: CcyPair <- ccyPairs)
        {
          rates.get(ccyPair) match {
            case Some(wr) => wr.setCurrentException(throwable)
            case None => nonReportedPairs = nonReportedPairs :+ ccyPair
          }
        }

      if (nonReportedPairs.nonEmpty) log.log(Level.WARNING,s"$nonReportedPairs had the error $throwable.getMessage")
    }
  }

  //  @throws(classOf[provisionOfService.ErrorInProvisionOfService])
  //  @throws(classOf[provisionOfService.ErrorInProvisionOfService])
  //  @throws(classOf[provisionOfService.ErrorWithConnection])
  //  @throws(classOf[provisionOfService.ErrorRateStale])
  //  @throws(classOf[provisionOfService.TooManyRequests])
  //  @throws(classOf[provisionOfService.ErrorNoRate])
  //  @throws(classOf[usageOfService.ErrorInUsageOfService])
  //  @throws(classOf[usageOfService.ErrorWithCurrencyGiven])
  //  @throws(classOf[java.lang.Throwable])
  def get(ccyPair: CcyPair): OneFrameRate = {
    log.log(Logger.Level.TRACE, s"getting $ccyPair")

    val maybeRateWrapper = ratesStore.getRate(ccyPair)
    val rateWrapper = if (maybeRateWrapper.isEmpty) add(ccyPair) else maybeRateWrapper.get
    val rate = rateWrapper.getRate

//    if(rateWrapper.getReadTimestamp.)
    // TODO error   if r.timestamp makes rate stale i.e. older than 5 mins
    log.log(Logger.Level.DEBUG, s"got $rate")

    rate
  }

  def fill(ccyPairs: CcyPairs): Unit = {
    log.log(Logger.Level.TRACE, "filling rateStore "+ccyPairs)

    for (ccyPair <- ccyPairs) {
      val oneFrameRate = oneFrame.getRate(ccyPair) // TODO maybe catch some exceptions
      ratesStore.add(oneFrameRate)
    }

    log.log(Logger.Level.DEBUG, s"filled with ${ccyPairs.size} rates")
  }

  def add(ccyPair: CcyPair): RateWrapper = {
    log.log(Logger.Level.TRACE, "adding" + ccyPair)

    val initialOneFrameRate = oneFrame.getRate(ccyPair) // throws exceptions
    val wrappedRate =ratesStore.add(initialOneFrameRate)
    val streamReader = new OneFrameRateStreamReader(ratesStore, oneFrame.getStreamingRates(Seq(ccyPair)))
    streamReader.start()

    log.log(Logger.Level.DEBUG, "added" + initialOneFrameRate)
    wrappedRate
  }

}


case class OneFrameRate(ccyPair: CcyPair, price: Double, timestamp: Timestamp)


