package forex.services.rates

import java.lang.System.Logger

class RatesStoreSupplier(rateStore: RatesStore, oneFrame: OneFrameService, ccyPairs: Seq[String] = Seq("JPYNZD","JPYUSD") ) {

//  var ccyPairs = Seq("JPYNZD","JPYUSD")

  var log: Logger = System.getLogger(this.getClass.getName)

  def fill(): Unit =
  {
    for(ccyPair <- ccyPairs )
      {
        val rate = oneFrame.getRate(ccyPair)
        rateStore.rates += (ccyPair->rate)
        log.log(Logger.Level.INFO,rate)
      }
  }

}
