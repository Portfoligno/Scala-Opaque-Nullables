package nullables.test

import nullables.internal.LiftedNull
import nullables.{NonNull, Null, Nullable}
import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NonNullSpec extends FreeSpec {
  "`NonNull`" - {
    "`apply` should work" in {
      assert(NonNull(Null) === LiftedNull(null))
      assert(NonNull(NonNull(Null)) === LiftedNull(LiftedNull(null)))
      assert(NonNull(NonNull(NonNull(Null))) === LiftedNull(LiftedNull(LiftedNull(null))))

      assert(NonNull("") === "")
    }
    "`unapply` should work" in {
      Nullable.fromInherentNullable("") match {
        case NonNull("") =>
      }

      NonNull(Null) match {
        case NonNull(null) =>
      }
      NonNull("") match {
        case NonNull("") =>
      }
    }

    "`value` should work" in {
      assert(NonNull(Null).value === Null)
      assert(NonNull("").value === "")
      assert(NonNull(0).value === 0)
    }
  }
}
