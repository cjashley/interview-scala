
val seq = Seq("a","b","c")
val arr = Array("a","b","c")
var ss =""
for(s <- seq) { ss += s }
ss


String.join("&", "a","b")

arr.mkString(",")
seq.mkString("param=","&","")

var params = for (elem <- seq) yield { "param="+elem }
params.mkString("&")

