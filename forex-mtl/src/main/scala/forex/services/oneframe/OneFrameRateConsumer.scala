package forex.services.oneframe

import forex.domain.Ccy.CcyPairs

trait OneFrameRateConsumer
{
  def consumeRate(oneFrameRate: OneFrameRate): Unit

  def consumeRateErrors(ccyPairs: CcyPairs, throwable: Throwable): Unit
}
