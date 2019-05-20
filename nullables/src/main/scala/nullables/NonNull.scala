package nullables

import nullables.internal.LiftedNull
import nullables.ops.NullableOps

object NonNull {
  def apply[A](value: A): NonNull[A] = {
    val r =
      value match {
        case n: LiftedNull => LiftedNull(n)
        case Null => LiftedNull(null)
        case _ => value
      }

    r.asInstanceOf[NonNull[A]]
  }

  def unapply[A](value: Nullable[A]): NullableOps[A] =
    value
}
