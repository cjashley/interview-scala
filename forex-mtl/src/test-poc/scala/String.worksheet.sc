val s = "[some bracketed text]"

s.drop(1)
s.take(1)
s.takeRight(1)
s.dropRight(1)
s

s.contains('[')
s.contains(']')
val noBrackets = s.drop(1).dropRight(1)

var ss = s
if (ss.contains('[') && ss.contains(']')) ss.drop(1).dropRight(1) else ss
ss = noBrackets
if (ss.contains('[') && ss.contains(']')) ss.drop(1).dropRight(1) else ss


import forex.domain.Ccy

ss = ""
var a = 0
for (a <- 1 to 10)  { println(a) }

ss = ""
for (a <- 0 to 9)
{
    println(a+ " "+ss.length + " = " + ss)
    val exptCcyValid = ss.length==3
    val exptCcyPairValid = ss.length==6
    assert(Ccy.isValid(ss) == exptCcyValid, ss)
    assert(Ccy.isValidPair(ss) == exptCcyPairValid, ss)
    ss += 'X'
}
// }

