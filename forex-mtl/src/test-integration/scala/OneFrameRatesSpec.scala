import forex.UnitSpec
import forex.services.oneframe.oneFrameRates

final class OneFrameRatesSpec extends UnitSpec {
"A RateStoreSupplier" should "should supply up to date rates"

  it should "fill ratesStore with rates fetched from OneFrame" in {

    // check all ccyPairs were fetched
    for(ccyPair <- oneFrameRates.ccyPairsToFetch)
      {
         val rate = oneFrameRates.get(ccyPair)
         rate.ccyPair should be (ccyPair)
      }
  }


  it should "TODO be continuously updating rates with newer rates fetched from OneFrame" taggedAs NotImplementedYet in {

    fail(NotImplementedYet.toString)
//    rss.getRate("NZDUSD")

  }
}
