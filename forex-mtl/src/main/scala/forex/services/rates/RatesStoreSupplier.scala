package forex.services.rates

import forex.domain.Ccy.{CcyPair, CcyPairs}

import java.lang.System.Logger

class RatesStoreSupplier(rateStore: RatesStore, oneFrame: OneFrameService, ccyPairs: Seq[String] = Seq("JPYNZD","JPYUSD") ) {

//  var ccyPairs = Seq("JPYNZD","JPYUSD")

  var log: Logger = System.getLogger(this.getClass.getName)
  var ccyPairsToFetch: CcyPairs = ccyPairs

  def fill(): RatesStoreSupplier = {
    for (ccyPair <- ccyPairs) {
      val rate = oneFrame.getRate(ccyPair) // TODO get could fail
      rateStore.rates += (ccyPair -> rate)
      log.log(Logger.Level.INFO,"fill "+ rate)
    }
    this
  }

  // TODO rates need refreshing as well
  def add(ccyPair:CcyPair): Rate = {
    ccyPairsToFetch = ccyPairsToFetch :+ ccyPair
    val rate = oneFrame.getRate(ccyPair) // TODO get could fail
    rateStore.rates += (ccyPair -> rate)
    log.log(Logger.Level.INFO, "add"+rate)
    rate
  }

}
