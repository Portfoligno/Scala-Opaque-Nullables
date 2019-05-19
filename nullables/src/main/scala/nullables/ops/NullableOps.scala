package nullables.ops

import nullables.{InherentNullness, LiftedNull, NonNull, Nullable}

class NullableOps[+A](private val v: Nullable[A]) extends AnyVal {
  def isEmpty: Boolean =
    v == null

  def isDefined: Boolean =
    v != null

  def get: A =
    (v: Any) match {
      case LiftedNull(n) => n.asInstanceOf[A]
      case null => throw new NoSuchElementException("Null().get")
      case _ => v.asInstanceOf[A]
    }

  def getOrElse[B >: A](default: => B): B =
    (v: Any) match {
      case LiftedNull(n) => n.asInstanceOf[A]
      case null => default
      case _ => v.asInstanceOf[A]
    }

  def orNull[A1 >: A](implicit ev: InherentNullness[A1]): A1 =
    getOrElse(ev(null))

  def map[B](f: A => B): Nullable[B] =
    flatMap(a => NonNull(f(a)))

  def fold[B](ifEmpty: => B)(f: A => B): B =
    (v: Any) match {
      case LiftedNull(n) => f(n.asInstanceOf[A])
      case null => ifEmpty
      case _ => f(v.asInstanceOf[A])
    }

  def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    fold(null: Nullable[B])(f)

  def flatten[B](implicit ev: A <:< Nullable[B]): Nullable[B] =
    flatMap(ev)

  final def filter(p: A => Boolean): Nullable[A] =
    if (!exists(p)) null else v

  final def filterNot(p: A => Boolean): Nullable[A] =
    if (forall(p)) null else v

  def nonEmpty: Boolean =
    isDefined

  def withFilter(p: A => Boolean): NullableWithFilter[A] =
    new NullableWithFilter(v)(p)

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
    v match {
      case null => alternative
      case _ => v
    }

  def iterator: Iterator[A] =
    fold(Iterator.empty: Iterator[A])(Iterator.single)

  def toList: List[A] =
    fold(List.empty[A])(_ :: Nil)

  def toRight[X](left: => X): Either[X, A] =
    fold(Left(left): Either[X, A])(Right(_))

  def toLeft[X](right: => X): Either[A, X] =
    fold(Right(right): Either[A, X])(Left(_))

  def toOption: Option[A] =
    fold(None: Option[A])(Some(_))
}

class NullableWithFilter[+A](v: Nullable[A])(p: A => Boolean) {
  def map[B](f: A => B): Nullable[B] =
    v.filter(p).map(f)

  def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    v.filter(p).flatMap(f)

  def foreach[U](f: A => U): Unit =
    v.filter(p).foreach(f)

  def withFilter(q: A => Boolean): NullableWithFilter[A] =
    new NullableWithFilter[A](v)(x => p(x) && q(x))
}
