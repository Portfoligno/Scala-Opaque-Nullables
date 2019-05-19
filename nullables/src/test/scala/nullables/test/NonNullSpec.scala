package nullables.test

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner
import nullables.{LiftedNull, NonNull, Null, Nullable}

@RunWith(classOf[JUnitRunner])
class NonNullSpec extends FreeSpec {
  "`NonNull`" - {
    "`apply` should work" in {
      NonNull(null) === LiftedNull(null)
      NonNull(NonNull(null)) === LiftedNull(LiftedNull(null))
      NonNull(NonNull(NonNull(null))) === LiftedNull(LiftedNull(LiftedNull(null)))

      NonNull("") === ""
    }
    "`unapply` should work" in {
      Nullable.fromInherentNullable(null) match {
        case Null() =>
      }
      Nullable.fromInherentNullable(null) match {
        case null =>
      }
      Nullable.fromInherentNullable("") match {
        case NonNull("") =>
      }
    }

    "`value` should work" in {
      assert(NonNull(null: String).value === null)
      assert(NonNull("").value === "")
      assert(NonNull(0).value === 0)
    }
  }
}
