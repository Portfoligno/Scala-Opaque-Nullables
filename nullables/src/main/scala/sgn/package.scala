import sgn.ops.NullableOps

package object sgn {
  type InherentNullness[+A] = Null <:< A


  type Null = Nullable[Nothing] with Null.Tag

  type NonNull[+A] = Nullable[A] with NonNull.Tag

  type Nullable[+A] = Nullable.Base with Nullable.Tag


  object Null {
    private[sgn] trait Tag extends Nullable.Tag

    def apply(): Null =
      null

    def unapply(arg: Nullable[_]): Boolean =
      arg == null
  }

  object NonNull {
    private[sgn] trait Tag extends Nullable.Tag

    def apply[A](value: A): NonNull[A] = {
      val r =
        value match {
          case n: LiftedNullness => LiftedNullness(n)
          case null => LiftedNullness(null)
          case _ => value
        }

      r.asInstanceOf[NonNull[A]]
    }

    def unapply[A](value: NonNull[A]): NullableOps[A] =
      value
  }

  object Nullable {
    private[sgn] type Base = Any { type Tag }
    private[sgn] trait Tag extends Any

    def fromInherentNullable[A : InherentNullness](value: A): Nullable[A] =
      value.asInstanceOf[Nullable[A]]

    def toInherentNullable[A : InherentNullness](value: Nullable[A]): A =
      value.asInstanceOf[A]

    implicit def toNullableOps[A](value: Nullable[A]): NullableOps[A] =
      new NullableOps[A](value match {
        case null => null
        case _ => value
      })
  }
}
