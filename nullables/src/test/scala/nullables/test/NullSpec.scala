package nullables.test

import nullables.{Null, Nullable}
import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
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
