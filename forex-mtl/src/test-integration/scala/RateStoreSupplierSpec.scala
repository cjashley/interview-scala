import forex.UnitSpec
import forex.services.rates.{OneFrameService, Rate, RatesStore, RatesStoreSupplier}

final class RateStoreSupplierSpec extends UnitSpec {
"A RateStoreSupplier" should "should supply up to date rates"

  private val oneFrame   = new OneFrameService()
  private val ratesStore = new RatesStore()
  private val rss        = new RatesStoreSupplier(ratesStore, oneFrame).fill()

  it should "fill ratesStore with rates fetched from OneFrame" in {

    // check all ccyPairs were fetched
    for(ccyPair <- rss.ccyPairsToFetch)
      {
         val rateO:Option[Rate] = ratesStore.get(ccyPair)
         rateO.get.ccyPair should be (ccyPair)
      }
  }

  it should "be continuously updating rates with newer rates fetched from OneFrame" taggedAs NotImplementedYet in {

    fail(NotImplementedYet.toString)
//    rss.getRate("NZDUSD")

  }
}
