package nullables.test

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner
import nullables.{NonNull, Null, Nullable}

@RunWith(classOf[JUnitRunner])
class NullableSpec extends FreeSpec {
  "`Nullable`" - {
    "`fromInherentNullable` should work" in {
      assert(Nullable.fromInherentNullable(null: String) === null)
      assert(Nullable.fromInherentNullable("") === "")
    }

    "`isEmpty` should work" in {
      assert(Null().isEmpty)
      assert(!NonNull(null).isEmpty)
      assert(!NonNull("").isEmpty)
    }
    "`get` should work" in {
      assertThrows[NoSuchElementException](Null().get)
      assert(NonNull(null: String).get === null)
      assert(NonNull("").get === "")
    }
  }
}
