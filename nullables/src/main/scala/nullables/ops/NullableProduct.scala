package nullables.ops

import nullables.internal.{BoxedNull, LiftedNull}

class NullableProduct[+A] private[ops] (private val v: Any) extends AnyVal with Product {
  override
  def productElement(n: Int): Any =
    if (n == 0 && v != BoxedNull) {
      v match {
        case LiftedNull(x) => x
        case _ => v
      }
    } else {
      throw new IndexOutOfBoundsException(String.valueOf(n))
    }

  override
  def productArity: Int =
    if (v != BoxedNull) 1 else 0

  override
  def canEqual(that: Any): Boolean =
    true

  override
  def productPrefix: String =
    if (v != BoxedNull) "NonNull" else "Null"

  override
  def toString: String = {
    def go(v: Any): String =
      v match {
        case BoxedNull => "Null"
        case LiftedNull(x) => s"NonNull(${go(x)})"
        case _ => s"NonNull($v)"
      }

    go(v)
  }
}
