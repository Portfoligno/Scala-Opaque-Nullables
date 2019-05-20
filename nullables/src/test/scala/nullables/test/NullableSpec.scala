package nullables.test

import nullables.internal.LiftedNull
import nullables.{NonNull, Null, Nullable}
import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NullableSpec extends FreeSpec {
  "`Nullable`" - {
    "`fromInherentNullable` should work" in {
      assert(Nullable.fromInherentNullable(null) === Null)
      assert(Nullable.fromInherentNullable("") === "")
    }

    "`isEmpty` should work" in {
      assert(Null.isEmpty)
      assert(!NonNull(Null).isEmpty)
      assert(!NonNull("").isEmpty)
    }
    "`isDefined` should work" in {
      assert(!Null.isDefined)
      assert(NonNull(Null).isDefined)
      assert(NonNull("").isDefined)
    }
    "`get` should work" in {
      assertThrows[NoSuchElementException](Null.get)
      assert(NonNull(Null).get === Null)
      assert(NonNull("").get === "")
    }
    "`getOrElse` should work" in {
      assert(Null.getOrElse(1) === 1)
      assert(NonNull(Null).getOrElse("") === Null)
      assert(NonNull("").getOrElse("~") === "")
    }
    "`orInherentNull` should work" in {
      assert(Nullable.empty[String].orInherentNull === null)
      assert(NonNull(Null).orInherentNull === Null)
      assert(NonNull("").orInherentNull === "")
    }
    "`map` should work" in {
      assert(Null.map(_ => 1) === Null)
      assert(NonNull(Null).map(String.valueOf) === "null")
      assert(NonNull("").map(_.length) === 0)
    }
    "`fold` should work" in {
      assert(Null.fold(-1)(_ => 1) === -1)
      assert(NonNull(Null).fold("")(String.valueOf) === "null")
      assert(NonNull("").fold(-1)(_.length) === 0)
    }
    "`flatMap` should work" in {
      assert(Null.flatMap(_ => Null) === Null)
      assert(Null.flatMap(_ => NonNull(1)) === Null)
      assert(NonNull(Null).flatMap(_ => Null) === Null)
      assert(NonNull(Null).flatMap(s => NonNull(String.valueOf(s))) === "null")
      assert(NonNull("").flatMap(_ => Null) === Null)
      assert(NonNull("").flatMap(s => NonNull(s.length)) === 0)
    }
    "`flatten` should work" in {
      NonNull(NonNull(Null)).flatten === Null
      NonNull(NonNull(0)).flatten === 0
      NonNull(NonNull(NonNull(Null))).flatten === LiftedNull(null)
      NonNull(NonNull(NonNull(Null))).flatten.flatten === Null
      NonNull(NonNull(NonNull(0))).flatten.flatten === 0

      assertTypeError("""
        NonNull(NonNull(NonNull(0))).flatten.flatten.flatten
      """)
    }
  }
}
