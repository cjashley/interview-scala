package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.Ccy.{CcyPair, CcyPairs}
import forex.domain._
import forex.services.oneframe.OneFrameRates
import forex.services.rates.Algebra
import forex.services.rates.errors._
import forex.services.rates.interpreters.provisionOfService._
import forex.services.rates.interpreters.usageOfService.{ErrorInUsageOfService, ErrorWithCurrencyPairGiven}

import java.lang.System.Logger
import java.lang.System.Logger.Level
import java.time.ZonedDateTime

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

  val oneFrameRates:OneFrameRates = new OneFrameRates() //

  def fill(ccyPairs: CcyPairs): Unit = oneFrameRates.fill(ccyPairs)


  private final val log: Logger = System.getLogger(this.getClass.getName)


  override def getRate(pair: Rate.Pair): F[Error Either Rate] = {

    try {
      log.log(Level.DEBUG,s"getRate $pair")
      val ccyPair: CcyPair = s"${pair.from}${pair.to}"
      val oneFrameRate = oneFrameRates.get(ccyPair)

      Rate(pair, Price(BigDecimal(oneFrameRate.price)), Timestamp(oneFrameRate.timestamp.atOffset(ZonedDateTime.now().getOffset))).asRight[Error].pure[F] // returning rate
    }
    catch {
      case e: ErrorInProvisionOfService => mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
      case e: ErrorWithConnection =>  mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
      case e: ErrorRateStale =>  mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
      case e: TooManyRequests =>  mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
      case e: ErrorNoRate =>  mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
      case e: ErrorInUsageOfService =>  mkErrorInUsageOfService(e).asLeft[Rate].pure[F]
      case e: ErrorWithCurrencyPairGiven =>  mkErrorInUsageOfService(e).asLeft[Rate].pure[F]
      case e: java.lang.Throwable =>  mkErrorInProvisionOfService(e).asLeft[Rate].pure[F]
    }
  }

  override def getCurrencies(): F[Either[Error, Currencies]] =
    Currencies(Currency.all).asRight[Error].pure[F] // return our limited currencies list (OneFrame can accept many more pairs)

}