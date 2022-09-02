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