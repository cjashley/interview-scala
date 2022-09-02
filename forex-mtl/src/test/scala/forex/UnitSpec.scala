package forex

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.Tag

abstract class UnitSpec extends AnyFlatSpec with should.Matchers with
OptionValues with Inside with Inspectors
{
  object NotImplementedYet extends Tag("forex.UnitSpec.tags.NotImplementedYet")
}

