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


  it should "be continuously updating rates with newer rates fetched from OneFrame" in {

    val oneFrameRates: OneFrameRates = new OneFrameRates()
    val rate1 = oneFrameRates.get("NZDJPY")
    val rate2 = oneFrameRates.get("NZDJPY")
    rate2.timestamp should be (rate1.timestamp)
    Thread.sleep(2000)
    val rate3 = oneFrameRates.get("NZDJPY")

    rate3.timestamp should not be (rate1.timestamp)



  }
}
