val l:Either[Int, String] = Left(1)
val r:Either[Int, String] = Right("two")

l match {
  case Left(l) => println("LEFT "+l)
  case Right(r) => println("RIGHT "+r)
}

r match {
  case Left(l) => println("LEFT "+l)
  case Right(r) => println("RIGHT "+r)
}


def sqrt(x: Double): Either[String, Double] =
if (x >= 0) Right(math.sqrt(x));
else Left("Negative value " + x + " cannot be square-rooted.");

// or you could have, if you want to avoid typing .right inside `for` later
def sqrt0(x: Double): Either.RightProjection[String, Double] =
(if (x >= 0) Right(math.sqrt(x));
else Left("Negative value " + x + " cannot be square-rooted.")
).right;

def asin(x: Double): Either[String, Double] =
if (x > 1) Left("Too high for asin")
else if (x < -1) Left("Too low for asin")
else Right(math.asin(x));


// Now we try to chain some computations.
// In particular, we'll be computing sqrt(asin(x)).
// If one of them fails, the rest will be skipped
// and the error of the failing one will be returned
// as Left.

{ // try some computations
  for (i <- -5 to 5) {
    val input: Double = i / 4.0;
    val d: Either[String, Double] = Right(input);
    val result: Either[String, Double] =
    for (v <- d.right;
           r1 <- Left("ALWAYS ERROR");// asin(v).right;
           r2 <- sqrt(r1).right
    // or you could use:
    // r2 <- sqrt0(r1)
    ) yield r2;
      println(input + "\t->\t" + result);
  }
}

