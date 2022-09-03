
type CcyPair = String

def getPrice(ccyPair: CcyPair):Int ={
   100
}

getPrice("ABC")

val valPair = "ABCXYZ"

getPrice(valPair)

case class CurPair(ccyPair: CcyPair)

def getPrice2( curPair: CurPair) =
{
  100
}

val cp = getPrice2(CurPair("ABCXYZ"))


object Ccy
{
  type Ccy = String  // three characters long eg. JPY
  type CcyPair = String  // six characters long g. JPYGBP
  type CcyPairs = Seq[CcyPair]

  def isValid(ccy: Ccy) = ccy.length == 3
  def isValidPair(ccyPair: CcyPair) = ccyPair.length == 6

  def from(ccyPair: CcyPair): Ccy = ccyPair.substring(0,3)
  def to(ccyPair: CcyPair): Ccy = ccyPair.substring(3)
}

def getPrice3(ccyPair: Ccy.CcyPair)