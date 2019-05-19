package sgn.ops

import sgn.LiftedNull

class NullableOps[A] private[sgn] (private val value: Any) extends AnyVal {
  def isEmpty: Boolean =
    value == null

  def get: A = {
    val r =
      value match {
        case null => throw new NoSuchElementException("Nullable.get")
        case LiftedNull(n) => n
        case _ => value
      }

    r.asInstanceOf[A]
  }
}
