import forex.UnitSpec
import forex.domain.Ccy.CcyPairs
import forex.services.oneframe.OneFrameRates

final class OneFrameRatesSpec extends UnitSpec {
"A RateStoreSupplier" should "should supply up to date rates"

  it should "fill ratesStore with rates fetched from OneFrame" in {

    val oneFrameRates:OneFrameRates = new OneFrameRates()
    val ccyPairsToFetch: CcyPairs = Seq("NZDJPY","JPYNZD")

    oneFrameRates.fill(ccyPairsToFetch)

    // check all ccyPairs were fetched
    for(ccyPair <- ccyPairsToFetch)
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
