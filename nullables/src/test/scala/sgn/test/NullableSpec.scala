package sgn.test

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner
import sgn.{NonNull, Null, Nullable}

@RunWith(classOf[JUnitRunner])
class NullableSpec extends FreeSpec {
  "Nullable" - {
    "`fromInherentNullable` should work" in {
      assert(Nullable.fromInherentNullable(null: String) === null)
      assert(Nullable.fromInherentNullable("") === "")
    }
    "`toInherentNullable` should work" in {
      Nullable.toInherentNullable(null)
      Nullable.toInherentNullable(Null())
      Nullable.toInherentNullable(NonNull(""))

      assert(Nullable.toInherentNullable(null: Nullable[String]) === null)
      assert(Nullable.toInherentNullable(Null(): Nullable[String]) === null)
      assert(Nullable.toInherentNullable(NonNull(""): Nullable[String]) === "")
    }
  }
}
