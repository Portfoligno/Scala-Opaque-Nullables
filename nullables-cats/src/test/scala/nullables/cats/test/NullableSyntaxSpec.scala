package nullables.cats.test

import org.scalatest.freespec.AnyFreeSpec

class NullableSyntaxSpec extends AnyFreeSpec {
  import nullables.cats.syntax.nullable._

  "`null`" - {
    "should work" in {
      `null`[Int]
    }
  }
  "`Any`" - {
    "`nonNull` should work" in {
      3.nonNull
    }
  }
}
