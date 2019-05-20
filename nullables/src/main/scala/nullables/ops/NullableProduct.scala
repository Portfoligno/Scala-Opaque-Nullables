package nullables.ops

import nullables.{NonNull, Nullable}

class NullableProduct[+A] private[ops] (private val v1: NonNull[Nullable[A]]) extends AnyVal with Product {
  override
  def productElement(n: Int): Any = {
    if (n == 0) {
      val v = v1.value

      if (v.isDefined) {
        return v.get
      }
    }
    throw new IndexOutOfBoundsException(String.valueOf(n))
  }

  override
  def productArity: Int =
    if (v1.value.isDefined) 1 else 0

  override
  def canEqual(that: Any): Boolean =
    true

  override
  def productPrefix: String =
    if (v1.value.isDefined) "NonNull" else "Null"
}
