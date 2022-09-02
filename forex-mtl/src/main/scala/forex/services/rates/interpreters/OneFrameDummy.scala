package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain._
import forex.services.rates.Algebra
import forex.services.rates.errors._

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  override def getRate(pair: Rate.Pair): F[Error Either Rate] =
    {

      Rate(pair, Price(BigDecimal(100)), Timestamp.now).asRight[Error].pure[F] // TODO get rate via OneFrame
    }

  override def getCurrencies(): F[Either[Error, Currencies]] =
    Currencies(Currency.all).asRight[Error].pure[F] // return our limited currencies list (OneFrame can accept many more pairs)

}
