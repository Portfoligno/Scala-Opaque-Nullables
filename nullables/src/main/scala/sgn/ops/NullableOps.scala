package sgn.ops

import sgn.{BoxedNull, LiftedNullness}

class NullableOps[A] private[sgn] (private val value: Any) extends AnyVal {
  def isEmpty: Boolean =
    value == BoxedNull

  def get: A = {
    val r =
      value match {
        case BoxedNull => throw new NoSuchElementException()
        case LiftedNullness(n) => n
        case _ => value
      }

    r.asInstanceOf[A]
  }
}
