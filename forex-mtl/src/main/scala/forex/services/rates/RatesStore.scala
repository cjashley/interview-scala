package forex.services.rates

import forex.domain.Timestamp


case class Rate (ccyPair: String, price: Double, timestamp: Timestamp)

case class RateWrapper (rate: Rate) // TODO

final class RatesStore {

  var rates = new scala.collection.mutable.HashMap[String, Rate]()

  def get(ccyPair: String): Option[Rate] =
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

