package forex.domain
import forex.UnitSpec
import org.scalatest.matchers._

class CurrencySpec extends UnitSpec with should.Matchers {

  "A Currency" should "convert to and from Strings to Currency and back" in {

    Currency.fromString("NZD") should be (Currency.NZD)
    Currency.fromString("AUD") should be (Currency.AUD)
    Currency.fromString("CAD") should be (Currency.CAD)
    Currency.fromString("CHF") should be (Currency.CHF)
    Currency.fromString("EUR") should be (Currency.EUR)
    Currency.fromString("GBP") should be (Currency.GBP)
    Currency.fromString("NZD") should be (Currency.NZD)
    Currency.fromString("JPY") should be (Currency.JPY)
    Currency.fromString("SGD") should be (Currency.SGD)
    Currency.fromString("USD") should be (Currency.USD)
    }
}
