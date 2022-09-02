import forex.UnitSpec
import forex.services.rates.OneFrameService

final class RateStoreSupplierSpec extends UnitSpec {
"A RateStoreSupplier" should "connect to OneFrame and return data"

  it should "get a rate with OneFrameService" in {
    val oneFrame = new OneFrameService()

    val rate = oneFrame.getRate("NZDUSD")

    rate.ccyPair should be("NZDUSD")
  }
}
