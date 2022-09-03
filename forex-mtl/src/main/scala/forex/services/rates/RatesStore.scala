package forex.services.rates

import forex.domain.Ccy.CcyPair
import forex.domain.Timestamp


case class Rate (ccyPair: CcyPair, price: Double, timestamp: Timestamp)

case class RateWrapper (rate: Rate) // TODO

final class RatesStore {

  var rates = new scala.collection.mutable.HashMap[CcyPair, Rate]()

  def get(ccyPair: CcyPair): Option[Rate] =
  {
    rates.get(ccyPair)
  }
}

//
//object RatesStore
//{
//
//  rates += ("" -> Rate("",1))
//}

