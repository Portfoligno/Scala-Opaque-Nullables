package nullables.ops

import nullables.NonNull

class NonNullOps[+A] private[nullables] (private val v: NonNull[A]) extends AnyVal {
  def value: A =
    v.get
}
