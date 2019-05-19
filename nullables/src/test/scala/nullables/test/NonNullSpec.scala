package nullables.test

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner
import nullables.{LiftedNull, NonNull, Null, Nullable}

@RunWith(classOf[JUnitRunner])
class NonNullSpec extends FreeSpec {
  "NonNull" - {
    "`apply` should work" in {
      NonNull(null) === LiftedNull(null)
      NonNull(LiftedNull(null)) === LiftedNull(LiftedNull(null))
      NonNull(LiftedNull(LiftedNull(null))) === LiftedNull(LiftedNull(LiftedNull(null)))

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
  }
}
