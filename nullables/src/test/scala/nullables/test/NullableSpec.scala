package nullables.test

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner
import nullables.{NonNull, Null, Nullable}

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

    "isEmpty should work" in {
      assert(Null().isEmpty)
      assert(!NonNull(null).isEmpty)
      assert(!NonNull("").isEmpty)
    }
    "get should work" in {
      assertThrows[NoSuchElementException](Null().get)
      assert(NonNull(null).get === null)
      assert(NonNull("").get === "")
    }
  }
}
