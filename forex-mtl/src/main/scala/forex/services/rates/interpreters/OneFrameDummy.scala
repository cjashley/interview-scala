package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.Ccy.CcyPair
import forex.domain._
import forex.services.oneframe._
import forex.services.rates.Algebra
import forex.services.rates.errors._

import java.lang.System.Logger
import forex.domain.{Currencies, Price, Rate}
import forex.services.rates.interpreters.provisionOfService.{ErrorInProvisionOfService, ErrorNoRate, ErrorRateStale, ErrorWithConnection, TooManyRequests}
import forex.services.rates.interpreters.usageOfService.{ErrorInUsageOfService, ErrorWithCurrencyPairGiven}

import java.lang.System.Logger.Level

object provisionOfService
{
  case class ErrorInProvisionOfService(message: String = "", cause: Option[Throwable] = None) extends Exception(message)  { cause.foreach(initCause) }
  case class ErrorWithConnection(message: String = "", cause: Option[Throwable] = None) extends Exception(message) { cause.foreach(initCause) }
  case class ErrorRateStale(message: String = "", cause: Option[Throwable] = None) extends Exception(message)  { cause.foreach(initCause) }
  case class ErrorNoRate(message: String = "", cause: Option[Throwable] = None) extends Exception(message)  { cause.foreach(initCause) }
  case class TooManyRequests(message: String = "", cause: Option[Throwable] = None) extends Exception(message)   { cause.foreach(initCause) }
}

object usageOfService
{
  case class ErrorInUsageOfService(message: String = "", cause: Option[Throwable] = None) extends Exception(message) { cause.foreach(initCause) }
  case class ErrorWithCurrencyPairGiven(message: String = "", cause: Option[Throwable] = None) extends Exception(message) { cause.foreach(initCause) }
}

// returns [Rate] or [Error.ErrorInUsageOfService]) or [Error.ErrorInProvisionOfService])
class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  private final val log: Logger = System.getLogger(this.getClass.getName)

  override def getRate(pair: Rate.Pair): F[Error Either Rate] = {

    try {
      log.log(Level.DEBUG,s"getRate $pair")
      val ccyPair: CcyPair = s"${pair.from}${pair.to}"
      val oneFrameRate = oneFrameRates.get(ccyPair)

      Rate(pair, Price(BigDecimal(oneFrameRate.price)), oneFrameRate.timestamp).asRight[Error].pure[F] // returning rate
    }
    catch {
      case e: ErrorInProvisionOfService => mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: ErrorWithConnection =>  mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: ErrorRateStale =>  mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: TooManyRequests =>  mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: ErrorNoRate =>  mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: ErrorInUsageOfService =>  mkErrorInUsageOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: ErrorWithCurrencyPairGiven =>  mkErrorInUsageOfService(e.getMessage).asLeft[Rate].pure[F]
      case e: java.lang.Throwable =>  mkErrorInProvisionOfService(e.getMessage).asLeft[Rate].pure[F]
    }
  }

  override def getCurrencies(): F[Either[Error, Currencies]] =
    Currencies(Currency.all).asRight[Error].pure[F] // return our limited currencies list (OneFrame can accept many more pairs)

}