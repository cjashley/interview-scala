import forex.domain.Currency

val list = List("a","b","c")

val pairs = for(x <- list; y <- list) yield (x, y)

val uniquePairs = for {
  (x, idxX) <- list.zipWithIndex
  (y, idxY) <- list.zipWithIndex
  if idxX != idxY
//  if idxX < idxY
} yield (x, y)


list.combinations(2)

list.combinations(2).toList

Currency.all.combinations(2).toList.size