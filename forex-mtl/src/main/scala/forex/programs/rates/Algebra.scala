package forex.programs.rates

import forex.domain.{Currencies, Rate}
import forex.programs.rates.errors._

trait Algebra[F[_]] {
  def get(request: Protocol.GetRatesRequest): F[Error Either Rate]
  def get(request: Protocol.GetCurrenciesRequest): F[Error Either Currencies]
}
