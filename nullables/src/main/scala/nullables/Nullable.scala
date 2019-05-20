package nullables

object Nullable {
  def empty[A]: Nullable[A] =
    Null

  def fromInherentNullable[A : InherentNullness](value: A): Nullable[A] =
    value.asInstanceOf[Nullable[A]]

  def fromOption[A](option: Option[A]): Nullable[A] =
    option match {
      case Some(a) => NonNull(a)
      case None => Null
    }
}
