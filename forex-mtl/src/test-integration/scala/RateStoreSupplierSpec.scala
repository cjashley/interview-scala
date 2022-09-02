import forex.UnitSpec
import forex.services.rates.{OneFrameService, Rate, RatesStore, RatesStoreSupplier}

final class RateStoreSupplierSpec extends UnitSpec {
"A RateStoreSupplier" should "should supply up to date rates"

  it should "fill ratesStore with rates fetched from OneFrame" in {

    val oneFrame = new OneFrameService()

    val ratesStore = new RatesStore()
    val rrs = new RatesStoreSupplier(ratesStore, oneFrame)

    rrs.fill()
    // check all ccyPairs were fetched
    for(ccyPair <- rrs.ccyPairsToFetch)
      {
         val rateO:Option[Rate] = ratesStore.get(ccyPair)
         rateO.get.ccyPair should be (ccyPair)
      }
  }
}
