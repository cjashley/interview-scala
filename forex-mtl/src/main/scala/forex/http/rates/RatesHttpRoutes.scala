package forex.http
package rates

import cats.effect.Sync
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.{ Protocol => RatesProgramProtocol }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class RatesHttpRoutes[F[_]: Sync](ratesProg: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._, QueryParams._, Protocol._

  private[http] val ratesPrefixPath = "/rates"
  private[http] val CurrenciesPrefixPath = "/currencies"

  private val httpRateRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(from) +& ToQueryParam(to) =>
      ratesProg.get(RatesProgramProtocol.GetRatesRequest(from, to)).
        flatMap(Sync[F].fromEither).flatMap { rate =>
        Ok(rate.asGetApiResponse)
      }
  }

  private val httpCurrenciesRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      ratesProg.get(RatesProgramProtocol.GetCurrenciesRequest()).
        flatMap(Sync[F].fromEither).flatMap { currencies =>
        Ok(currencies.asGetApiResponse)
      }
  }

  val routes: HttpRoutes[F] = Router(
    ratesPrefixPath -> httpRateRoutes
      , CurrenciesPrefixPath -> httpCurrenciesRoutes
  )


}
