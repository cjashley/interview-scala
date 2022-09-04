package forex.domain
import forex.UnitSpec
import org.scalatest.matchers._

class CurrencySpec extends UnitSpec with should.Matchers {

  "A Currency" should "convert to and from Strings to Currency and back" in {

    //  test version 1 one currency at a time
    Currency.fromString("NZD") should be(Currency.NZD)
    Currency.fromString("AUD") should be(Currency.AUD)
    Currency.fromString("CAD") should be(Currency.CAD)
    Currency.fromString("CHF") should be(Currency.CHF)
    Currency.fromString("EUR") should be(Currency.EUR)
    Currency.fromString("GBP") should be(Currency.GBP)
    Currency.fromString("NZD") should be(Currency.NZD)
    Currency.fromString("JPY") should be(Currency.JPY)
    Currency.fromString("SGD") should be(Currency.SGD)
    Currency.fromString("USD") should be(Currency.USD)

    // test version 2 making use of all list containing all currencies
    for (c <- Currency.all)   {
      Currency.fromString(c.toString) should be(c)  // should be test format
      assert(Currency.fromString(c.toString) == c)  // plain assert version
    }
    }

  // test for learning scala, not needed unless changed to more specific exception
  it should "throw MatchError if invalid currency" in {
    intercept[MatchError] {
      Currency.fromString("UNKNOWN")
    }
  }
}
