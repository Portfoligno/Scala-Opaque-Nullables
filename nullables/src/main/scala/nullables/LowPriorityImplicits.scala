package nullables

private[nullables]
trait LowPriorityImplicits {
  implicit def nullablesNullableToIterable[A](value: Nullable[A]): Iterable[A] =
    value.toList
}
