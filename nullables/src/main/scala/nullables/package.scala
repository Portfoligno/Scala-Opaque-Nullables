import nullables.internal.LiftedNull
import nullables.ops.{NonNullOps, NullableOps}

package object nullables {
  type InherentNullness[+A] = scala.Null <:< A


  type Null = Nullable.Base with Null.Tag

  type NonNull[+A] = Nullable.Base with NonNull.Tag[A]

  type Nullable[+A] = Nullable.Base with Nullable.Tag[A]


  object Null {
    private[nullables] trait Tag extends Nullable.Tag[Nothing]

    def apply(): Null =
      null

    def unapply(arg: Nullable[_]): Boolean =
      arg == null
  }

  object NonNull {
    private[nullables] trait Tag[+A] extends Nullable.Tag[A]

    def apply[A](value: A): NonNull[A] = {
      val r =
        value match {
          case n: LiftedNull => LiftedNull(n)
          case null => LiftedNull(null)
          case _ => value
        }

      r.asInstanceOf[NonNull[A]]
    }

    def unapply[A](value: Nullable[A]): NullableOps[A] =
      value

    implicit def toNonNullOps[A](value: NonNull[A]): NonNullOps[A] =
      new NonNullOps[A](value)
  }

  object Nullable {
    private[nullables] type Base = Any { type Tag }
    private[nullables] trait Tag[+A] extends Any

    def empty[A]: Nullable[A] =
      null

    def fromInherentNullable[A : InherentNullness](value: A): Nullable[A] =
      value.asInstanceOf[Nullable[A]]

    def fromOption[A](option: Option[A]): Nullable[A] =
      option match {
        case Some(a) => NonNull(a)
        case None => Null()
      }

    implicit def toNullableOps[A](value: Nullable[A]): NullableOps[A] =
      new NullableOps[A](value)
  }
}
