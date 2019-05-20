import nullables.ops.{NonNullOps, NullableOps, NullableProduct}

package object nullables {
  type InherentNullness[+A] = scala.Null <:< A

  type Null = Base with NullTag
  type NonNull[+A] = Base with NonNullTag[A]
  type Nullable[+A] = Base with NullableTag[A]

  private[nullables] type Base = Any { type Tag }
  private[nullables] trait NonNullTag[+A] extends NullableTag[A]
  private[nullables] trait NullTag extends NullableTag[Nothing]
  private[nullables] sealed trait NullableTag[+A] extends Any

  val Null: Null = null


  implicit def toNonNullOps[A](value: NonNull[A]): NonNullOps[A] =
    new NonNullOps[A](value)

  implicit def toNullableOps[A](value: Nullable[A]): NullableOps[A] =
    new NullableOps[A](value)

  implicit def toNullableProduct[A](value: Nullable[A]): NullableProduct[A] =
    new NullableProduct[A](NonNull(value))
}
