package forex.services.rates

import forex.domain.{Currencies, Rate}
import errors._

trait Algebra[F[_]] {
  def getRate(pair: Rate.Pair): F[Error Either Rate]
  def getCurrencies(): F[Error Either Currencies]
}
