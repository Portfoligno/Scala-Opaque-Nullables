package nullables

private[nullables]
trait LowPriorityImplicits {
  implicit def toIterable[A](value: Nullable[A]): Iterable[A] =
    value.toList
}
