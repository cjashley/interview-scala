package forex.http.rates

import forex.domain._

object Converters {
  import Protocol._

  private[rates] implicit class GetApiResponseOps(val rate: Rate) extends AnyVal {
    def asGetApiResponse: RatesApiResponse =
      RatesApiResponse(
        from = rate.pair.from,
        to = rate.pair.to,
        price = rate.price,
        timestamp = rate.timestamp
      )
  }

//  private[currencies]
  implicit class GetApiResponseCcy(val ccys: Currencies) extends AnyVal {
    def asGetApiResponse: CurrenciesApiResponse =
      CurrenciesApiResponse(
        currencies = ccys
      )
  }

}
