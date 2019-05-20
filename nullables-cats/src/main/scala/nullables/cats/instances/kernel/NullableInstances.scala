package nullables
package cats.instances.kernel

import _root_.cats.kernel._

trait NullableInstances extends NullableInstances0 {
  implicit def nullablesCatsKernelOrderForNullable[A: Order]: Order[Nullable[A]] =
    new NullableOrder[A]
  implicit def nullablesCatsKernelMonoidForNullable[A: Semigroup]: Monoid[Nullable[A]] =
    new NullableMonoid[A]
}

trait NullableInstances0 extends NullableInstances1 {
  implicit def nullablesCatsKernelPartialOrderForNullable[A: PartialOrder]: PartialOrder[Nullable[A]] =
    new NullablePartialOrder[A]
}

trait NullableInstances1 extends NullableInstances2 {
  implicit def nullablesCatsKernelHashForNullable[A: Hash]: Hash[Nullable[A]] =
    new NullableHash[A]
}

trait NullableInstances2 {
  implicit def nullablesCatsKernelEqForNullable[A: Eq]: Eq[Nullable[A]] =
    new NullableEq[A]
}

class NullableOrder[A](implicit A: Order[A]) extends Order[Nullable[A]] {
  def compare(x: Nullable[A], y: Nullable[A]): Int =
    x match {
      case Null =>
        if (y.isEmpty) 0 else -1
      case NonNull(a) =>
        y match {
          case Null    => 1
          case NonNull(b) => A.compare(a, b)
        }
    }
}

class NullablePartialOrder[A](implicit A: PartialOrder[A]) extends PartialOrder[Nullable[A]] {
  def partialCompare(x: Nullable[A], y: Nullable[A]): Double =
    x match {
      case Null =>
        if (y.isEmpty) 0.0 else -1.0
      case NonNull(a) =>
        y match {
          case Null    => 1.0
          case NonNull(b) => A.partialCompare(a, b)
        }
    }
}

class NullableHash[A](implicit A: Hash[A]) extends NullableEq[A]()(A) with Hash[Nullable[A]] {
  def hash(x: Nullable[A]): Int = x match {
    case Null     => nullablesToNullableOps(Null).hashCode()
    case NonNull(xx) => product1HashWithPrefix(A.hash(xx), x.productPrefix)
  }

  private
  def product1HashWithPrefix(_1Hash: Int, prefix: String): Int = {
    import scala.util.hashing.MurmurHash3._
    var h = productSeed
    h = mix(h, _1Hash)
    finalizeHash(h, 1)
  }
}

class NullableEq[A](implicit A: Eq[A]) extends Eq[Nullable[A]] {
  def eqv(x: Nullable[A], y: Nullable[A]): Boolean =
    x match {
      case Null => y.isEmpty
      case NonNull(a) =>
        y match {
          case Null    => false
          case NonNull(b) => A.eqv(a, b)
        }
    }
}

class NullableMonoid[A](implicit A: Semigroup[A]) extends Monoid[Nullable[A]] {
  def empty: Nullable[A] = Null
  def combine(x: Nullable[A], y: Nullable[A]): Nullable[A] =
    x match {
      case Null => y
      case NonNull(a) =>
        y match {
          case Null    => x
          case NonNull(b) => NonNull(A.combine(a, b))
        }
    }
}
