package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.Ccy.CcyPair
import forex.domain._
import forex.services.rates.{Algebra, OneFrameService, RatesStore, RatesStoreSupplier}
import forex.services.rates.errors._

import java.lang.System.Logger.Level

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  private val oneFrame = new OneFrameService()
  private val ratesStore = new RatesStore()
  private val rss = new RatesStoreSupplier(ratesStore, oneFrame).fill()

  private val logger:System.Logger = System.getLogger(this.getClass.getName)
  override def getRate(pair: Rate.Pair): F[Error Either Rate] =
    {
      val ccyPair:CcyPair = s"${pair.from}${pair.to}"
      val rateO = ratesStore.get(ccyPair)
      val r = if (rateO.isEmpty) rss.add(ccyPair) else {
        logger.log(Level.INFO,"rateStore rate "+rateO.get)
        rateO.get
      }
// TODO error   if r.timestamp makes rate stale i.e. older than 5 mins
      Rate(pair, Price(BigDecimal(r.price)), r.timestamp).asRight[Error].pure[F] // returning rate
    }

  override def getCurrencies(): F[Either[Error, Currencies]] =
    Currencies(Currency.all).asRight[Error].pure[F] // return our limited currencies list (OneFrame can accept many more pairs)

}
