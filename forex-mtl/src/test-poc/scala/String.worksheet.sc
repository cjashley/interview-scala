val s = "[some bracketed text]"
import java.time.Duration
var rateStaleDuration = Duration.ofMinutes(1)
rateStaleDuration.toString
s.drop(1)
s.take(1)
s.takeRight(1)
s.dropRight(1)
s

s.contains('[')
s.contains(']')
val noBrackets = s.drop(1).dropRight(1)

import io.circe.{Decoder, Json, ParsingFailure}
import io.circe.parser.parse

import java.time.Duration

var js = """[{"a":"val"},{"a":"val"}]"""
js = """[{"from":"USD","to":"JPY","bid":0.5596033484836841,"ask":0.6937973188546951,"price":0.6267003336691896,"time_stamp":"2022-09-04T14:01:03.108Z"}]"""
val jsonE: Either[ParsingFailure, Json] = parse(js)
val json = jsonE.toOption.get
json.isArray
val a = json.asArray
a.mkString


for(v <- json.asArray) println(v.mkString)


js = """[{"from":"NZD","to":"JPY","bid":0.9166090067283655,"ask":0.07903372827701083,"price":0.497821367502688165,"time_stamp":"2022-09-04T13:49:35.605Z"}]"""

var ss = s
if (ss.contains('[') && ss.contains(']')) ss.drop(1).dropRight(1) else ss
ss = noBrackets
if (ss.contains('[') && ss.contains(']')) ss.drop(1).dropRight(1) else ss


import forex.domain.Ccy
import io.circe.{Json, ParsingFailure}
import io.circe.parser.parse

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

