package forex.domain

import forex.UnitSpec

class CcySpec extends UnitSpec {

  "A Ccy" should "encapsulate currency identifiers"

  it should "validate the lengths of Ccy and CcyPair" in {
    var ss = ""

    for ( s <- 1 to 10) {
      val exptCcyValid = ss.length == 3
      val exptCcyPairValid = ss.length == 6
      val isCcyValid = Ccy.isValid(ss)
      val isCcyValidPair = Ccy.isValidPair(ss)
      var postfix = ""
      if (isCcyValid) postfix += "isCcy"
      if (isCcyValidPair) postfix += "isCcyPair"
      println(s"$s $ss $postfix")
      assert( isCcyValid == exptCcyValid, ss)
      assert( isCcyValidPair == exptCcyPairValid, ss)
      ss += 'X'
    }
  }
}
