package nullables.ops

import nullables.{InherentNullness, LiftedNull, NonNull, Nullable}

class NullableOps[A](private val value: Nullable[A]) extends AnyVal {
  def isEmpty: Boolean =
    value == null

  def isDefined: Boolean =
    value != null

  def get: A =
    (value: Any) match {
      case LiftedNull(n) => n.asInstanceOf[A]
      case null => throw new NoSuchElementException("Null().get")
      case _ => value.asInstanceOf[A]
    }

  def getOrElse[B >: A](default: => B): B =
    (value: Any) match {
      case LiftedNull(n) => n.asInstanceOf[A]
      case null => default
      case _ => value.asInstanceOf[A]
    }

  def orNull[A1 >: A](implicit ev: InherentNullness[A1]): A1 =
    getOrElse(ev(null))

  def map[B](f: A => B): Nullable[B] =
    flatMap(a => NonNull(f(a)))

  def fold[B](ifEmpty: => B)(f: A => B): B =
    (value: Any) match {
      case LiftedNull(n) => f(n.asInstanceOf[A])
      case null => ifEmpty
      case _ => f(value.asInstanceOf[A])
    }

  def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    fold(null: Nullable[B])(f)

  def flatten[B](implicit ev: A <:< Nullable[B]): Nullable[B] =
    flatMap(ev)

  final def filter(p: A => Boolean): Nullable[A] =
    if (!exists(p)) null else value

  final def filterNot(p: A => Boolean): Nullable[A] =
    if (forall(p)) null else value

  def nonEmpty: Boolean =
    isDefined

  // withFilter

  def contains[A1 >: A](elem: A1): Boolean =
    exists(_ == elem)

  def exists(p: A => Boolean): Boolean =
    fold(false)(p)

  def forall(p: A => Boolean): Boolean =
    fold(true)(p)

  def foreach[U](f: A => U): Unit =
    map(f)

  def collect[B](pf: PartialFunction[A, B]): Nullable[B] =
    flatMap(pf.lift.andThen(Nullable.fromOption))

  def orElse[B >: A](alternative: => Nullable[B]): Nullable[B] =
    value match {
      case null => alternative
      case _ => value
    }

  def iterator: Iterator[A] =
    fold(Iterator.empty: Iterator[A])(Iterator.single)

  def toList: List[A] =
    fold(List.empty[A])(_ :: Nil)

  def toRight[X](left: => X): Either[X, A] =
    fold(Left(left): Either[X, A])(Right(_))

  def toLeft[X](right: => X): Either[A, X] =
    fold(Right(right): Either[A, X])(Left(_))
}
