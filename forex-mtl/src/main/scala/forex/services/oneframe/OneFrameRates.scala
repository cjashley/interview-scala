package forex.services.oneframe

import forex.domain.Ccy._
import forex.domain.{Timestamp}

import java.lang.System.Logger
import java.time.Instant


case class oneFrameRate(ccyPair: CcyPair, price: Double, timestamp: Timestamp)


object oneFrameRates  {
  private final val log: Logger = System.getLogger(this.getClass.getName)

  val oneFrame = new OneFrameService() // TODO rates need refreshing from streams
  val ratesStore = new RatesStore()
  var ccyPairsToFetch: CcyPairs = Seq()

  case class RateWrapper(protected var rate: oneFrameRate,
                         protected var readTimestamp: Instant,
                         protected var lastException: Option[Throwable],
                         protected var currentException: Option[Throwable]) {

    def getRate: oneFrameRate = {
      readTimestamp = Instant.now
      rate
    }

    def getReadTimestamp: Instant = readTimestamp

    def setRate(rate: oneFrameRate): Unit = {
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

    def apply(rate: oneFrameRate):RateWrapper = {
      new RateWrapper(rate, Instant.now(), Option.empty, Option.empty)
    }
  }

  final class RatesStore {

    private val rates = new scala.collection.mutable.HashMap[CcyPair, RateWrapper]()

    def add(oneFrameRate: oneFrameRate):RateWrapper =
    {
      val rateWrapper = RateWrapper(oneFrameRate)
      rates += (oneFrameRate.ccyPair -> rateWrapper)

      rateWrapper
    }

    def getRate(ccyPair: CcyPair): Option[RateWrapper] = rates.get(ccyPair)

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
  def get(ccyPair: CcyPair): oneFrameRate = {
    log.log(Logger.Level.TRACE, s"getting $ccyPair")

    val maybeRateWrapper = ratesStore.getRate(ccyPair)
    val rateWrapper = if (maybeRateWrapper.isEmpty) add(ccyPair) else maybeRateWrapper.get
    val rate = rateWrapper.getRate

//    if(rateWrapper.getReadTimestamp.)
    // TODO error   if r.timestamp makes rate stale i.e. older than 5 mins
    log.log(Logger.Level.DEBUG, s"got $rate")

    rate
  }

  def fill() = {
    log.log(Logger.Level.TRACE, "filling rateStore")

    for (ccyPair <- ccyPairsToFetch) {
      val oneFrameRate = oneFrame.getRate(ccyPair) // TODO maybe catch some exceptions
      ratesStore.add(oneFrameRate)
    }

    log.log(Logger.Level.DEBUG, s"filled with ${ccyPairsToFetch.size} rates")
  }

  def add(ccyPair: CcyPair): RateWrapper = {
    log.log(Logger.Level.TRACE, "adding" + ccyPair)

    ccyPairsToFetch = ccyPairsToFetch :+ ccyPair
    val initialOneFrameRate = oneFrame.getRate(ccyPair) // TODO maybe catch some exceptions
    val wrappedRate =ratesStore.add(initialOneFrameRate)

    log.log(Logger.Level.DEBUG, "added" + initialOneFrameRate)
    wrappedRate
  }
}


