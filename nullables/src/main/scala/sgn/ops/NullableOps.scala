package sgn.ops

import sgn.BoxedNull

class NullableOps[A] private[sgn] (private val value: Any) extends AnyVal {
  def isEmpty: Boolean =
    value == BoxedNull

  def get: A = {
    val r =
      value match {
        case BoxedNull => null
        case _ => value
      }

    r.asInstanceOf[A]
  }
}
