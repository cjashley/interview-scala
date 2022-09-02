package forex.http.rates

import cats.data.{Validated, ValidatedNel}
import forex.domain.Currency
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder, QueryParameterValue}

object QueryParams {

  private[http] implicit val currencyQueryParam: QueryParamDecoder[Currency] =
    new QueryParamDecoder[Currency]  {
      // https://stackoverflow.com/questions/67710359/decode-an-optional-query-parameter-using-queryparamdecoder-in-scala
      def decode(part: QueryParameterValue): ValidatedNel[ParseFailure, Currency] = {
        //           throw new ParseFailure("Invalid Currency","currency is unknown "+part)
        Validated.catchOnly[MatchError] {
          println("decode "+part.value)
          val c = Currency.fromString(part.value)
          println("decoded "+c)
          (c)
        }.leftMap(e => ParseFailure("Invalid query parameter part", e.getMessage))
          .toValidatedNel
      }
    }

  object FromQueryParam extends QueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends QueryParamDecoderMatcher[Currency]("to")

}
