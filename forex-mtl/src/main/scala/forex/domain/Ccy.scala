package forex.domain

object Ccy
{
  type Ccy = String  // three characters long eg. JPY
  type CcyPair = String  // six characters long g. JPYGBP
  type CcyPairs = Seq[CcyPair]

  def isValid(ccy: Ccy): Boolean = ccy.length == 3
  def isValidPair(ccyPair: CcyPair): Boolean = ccyPair.length == 6

  def from(ccyPair: CcyPair): Ccy = ccyPair.substring(0,3)
  def to(ccyPair: CcyPair): Ccy = ccyPair.substring(3)
}
