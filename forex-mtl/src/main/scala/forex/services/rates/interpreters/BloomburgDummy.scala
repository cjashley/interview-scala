package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.{Currencies, Rate}

import forex.services.rates.{Algebra, errors}
import forex.services.rates.errors._


class BloomburgDummy[F[_]: Applicative] extends Algebra[F] {

  override def getRate(pair: Rate.Pair): F[Error Either Rate]  = {
    errors.mkErrorInProvisionOfService("Bloomberg Not implemented").asLeft[Rate].pure[F]
  }

  override def getCurrencies(): F[Either[Error, Currencies]]  = {
    errors.mkErrorInProvisionOfService("Bloomberg Not implemented").asLeft[Currencies].pure[F]
//    new ErrorInUsageOfService("Not implemented").asLeft[Currencies].pure[F] // TODO why will this not complile?
  }
}
