package nullables.ops

import nullables.internal.{BoxedNull, LiftedNull}
import nullables.{InherentNullness, NonNull, Null, Nullable}

class NullableOps[+A] private[nullables] (private val v: Any) extends AnyVal with Product {
  def isEmpty: Boolean =
    v == BoxedNull

  def isDefined: Boolean =
    v != BoxedNull

  def get: A =
    v match {
      case BoxedNull => throw new NoSuchElementException("Null.get")
      case LiftedNull(n) => n.asInstanceOf[A]
      case _ => v.asInstanceOf[A]
    }

  def getOrElse[B >: A](default: => B): B =
    v match {
      case BoxedNull => default
      case LiftedNull(n) => n.asInstanceOf[A]
      case _ => v.asInstanceOf[A]
    }

  def orInherentNull[A1 >: A](implicit ev: InherentNullness[A1]): A1 =
    getOrElse(ev(null))

  def map[B](f: A => B): Nullable[B] =
    flatMap(a => NonNull(f(a)))

  def fold[B](ifEmpty: => B)(f: A => B): B =
    v match {
      case BoxedNull => ifEmpty
      case LiftedNull(n) => f(n.asInstanceOf[A])
      case _ => f(v.asInstanceOf[A])
    }

  def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    fold(Null: Nullable[B])(f)

  def flatten[B](implicit ev: A <:< Nullable[B]): Nullable[B] =
    flatMap(ev)

  final def filter(p: A => Boolean): Nullable[A] =
    if (!exists(p)) Null else v.asInstanceOf[Nullable[A]]

  final def filterNot(p: A => Boolean): Nullable[A] =
    if (forall(p)) Null else v.asInstanceOf[Nullable[A]]

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
      case BoxedNull => alternative
      case _ => v.asInstanceOf[Nullable[A]]
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

  override
  def productElement(n: Int): Any =
    if (n == 0 && v != BoxedNull) {
      v match {
        case LiftedNull(x) => x
        case _ => v
      }
    } else {
      throw new IndexOutOfBoundsException(String.valueOf(n))
    }

  override
  def productArity: Int =
    if (v != BoxedNull) 1 else 0

  override
  def canEqual(that: Any): Boolean =
    true

  override
  def productPrefix: String =
    if (v != BoxedNull) "NonNull" else "Null"

  def productString: String = {
    def go(v: Any): String =
      v match {
        case BoxedNull => "Null"
        case LiftedNull(x) => s"NonNull(${go(x)})"
        case _ => s"NonNull($v)"
      }

    go(v)
  }

  override
  def toString: String =
    productString
}

class NullableWithFilter[+A] private[ops] (v: Any)(p: A => Boolean) {
  def map[B](f: A => B): Nullable[B] =
    new NullableOps(v).filter(p).map(f)

  def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    new NullableOps(v).filter(p).flatMap(f)

  def foreach[U](f: A => U): Unit =
    new NullableOps(v).filter(p).foreach(f)

  def withFilter(q: A => Boolean): NullableWithFilter[A] =
    new NullableWithFilter[A](v)(x => p(x) && q(x))
}
