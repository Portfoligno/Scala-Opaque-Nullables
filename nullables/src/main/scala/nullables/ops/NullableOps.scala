package nullables.ops

import nullables.LiftedNull

class NullableOps[A] private[nullables] (private val value: Any) extends AnyVal {
  def isEmpty: Boolean =
    value == null

  def get: A = {
    val r =
      value match {
        case LiftedNull(n) => n
        case null => throw new NoSuchElementException("Nullable.get")
        case _ => value
      }

    r.asInstanceOf[A]
  }
}
