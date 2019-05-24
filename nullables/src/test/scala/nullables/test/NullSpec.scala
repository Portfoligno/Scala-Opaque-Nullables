package nullables.test

import nullables.{Null, Nullable}
import org.scalatest.FreeSpec

class NullSpec extends FreeSpec {
  "`Null`" - {
    "`unapply` should work" in {
      Nullable.fromInherentNullable(null) match {
        case Null =>
      }
      Nullable.fromInherentNullable(null) match {
        case null =>
      }
    }
  }
}
