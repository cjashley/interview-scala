import scala.collection.immutable.HashMap

val map = HashMap("Amit" -> "001", "Sumit" -> "002", "Aman" -> "003", "Sunita" -> "004", "Amita" -> "005", "Vinata" -> "006", "Komal" -> "007", "Nikita" -> "008")
map.values.foreach{ k =>
  println( "Value in the map is = " + k )
}

case class Rate (ccyPair: String, price: Double)

val m = new HashMap[String,Rate]()

val mm = m + ("NZDUSD" -> Rate("NZUSD",0.4))

mm.get("NZDUSD")
mm.get("NZDUSD").get

var n = new scala.collection.mutable.HashMap[String,Rate]()

val nn = n +  ("NZDUSD" -> Rate("NZDUSD",0.4))

n += ("NZDUSD" -> Rate("NZDUSD",0.4))

nn += ("USDNZD" -> Rate("USDNZD",0.4))

nn.get("NZDUSD")
nn.get("USDNZD")

n.get("NZDUSD")
n.get("USDNZD")

