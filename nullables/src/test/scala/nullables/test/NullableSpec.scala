package nullables.test

import nullables.internal.LiftedNull
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
    "`isDefined` should work" in {
      assert(!Null().isDefined)
      assert(NonNull(null).isDefined)
      assert(NonNull("").isDefined)
    }
    "`get` should work" in {
      assertThrows[NoSuchElementException](Null().get)
      assert(NonNull(null: String).get === null)
      assert(NonNull("").get === "")
    }
    "`getOrElse` should work" in {
      assert(Null().getOrElse(1) === 1)
      assert(NonNull(null: String).getOrElse("") === null)
      assert(NonNull("").getOrElse("~") === "")
    }
    "`orNull` should work" in {
      assert(Nullable.empty[String].orNull === null)
      assert(NonNull(null: String).orNull === null)
      assert(NonNull("").orNull === "")
    }
    "`map` should work" in {
      assert(Null().map(_ => 1) === null)
      assert(NonNull(null: String).map(String.valueOf) === "null")
      assert(NonNull("").map(_.length) === 0)
    }
    "`fold` should work" in {
      assert(Null().fold(-1)(_ => 1) === -1)
      assert(NonNull(null: String).fold("")(String.valueOf) === "null")
      assert(NonNull("").fold(-1)(_.length) === 0)
    }
    "`flatMap` should work" in {
      assert(Null().flatMap(_ => Null()) === null)
      assert(Null().flatMap(_ => NonNull(1)) === null)
      assert(NonNull(null: String).flatMap(_ => Null()) === null)
      assert(NonNull(null: String).flatMap(s => NonNull(String.valueOf(s))) === "null")
      assert(NonNull("").flatMap(_ => Null()) === null)
      assert(NonNull("").flatMap(s => NonNull(s.length)) === 0)
    }
    "`flatten` should work" in {
      NonNull(NonNull(null)).flatten === null
      NonNull(NonNull(0)).flatten === 0
      NonNull(NonNull(NonNull(null))).flatten === LiftedNull(null)
      NonNull(NonNull(NonNull(null))).flatten.flatten === null
      NonNull(NonNull(NonNull(0))).flatten.flatten === 0

      assertTypeError("""
        NonNull(NonNull(NonNull(0))).flatten.flatten.flatten
      """)
    }
  }
}
