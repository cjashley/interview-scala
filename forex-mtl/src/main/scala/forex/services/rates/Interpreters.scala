package forex.services.rates

import cats.Applicative
import interpreters._

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = {
    val d = new OneFrameDummy[F]()
    d.fill(Seq("JPYGBP")) // TODO pre-load all sequences
    d
  }

}
