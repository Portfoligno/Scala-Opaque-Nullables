package nullables.cats.syntax

import cats.{Applicative, ApplicativeError, Monoid}
import cats.data.{Ior, OptionT, Validated, ValidatedNec, ValidatedNel}
import nullables.cats.syntax.NullableOps.LiftToPartiallyApplied
import nullables.{NonNull, Null, Nullable}

trait NullableSyntax {
  def `null`[A]: Nullable[A] =
    Null

  implicit def toNullableIdOps[A](value: A): NullableIdOps[A] =
    new NullableIdOps[A](value)

  implicit def toNullableOps[A](value: Nullable[A]): NullableOps[A] =
    new NullableOps[A](value)
}

class NullableIdOps[A](private val value: A) extends AnyVal {
  def nonNull: Nullable[A] =
    NonNull(value)
}

class NullableOps[A](private val oa: Nullable[A]) extends AnyVal {
  def toInvalid[B](b: => B): Validated[A, B] = oa.fold[Validated[A, B]](Validated.Valid(b))(Validated.Invalid(_))

  def toInvalidNel[B](b: => B): ValidatedNel[A, B] =
    oa.fold[ValidatedNel[A, B]](Validated.Valid(b))(Validated.invalidNel)

  def toInvalidNec[B](b: => B): ValidatedNec[A, B] =
    oa.fold[ValidatedNec[A, B]](Validated.Valid(b))(Validated.invalidNec)

  def toValid[B](b: => B): Validated[B, A] = oa.fold[Validated[B, A]](Validated.Invalid(b))(Validated.Valid(_))

  def toValidNel[B](b: => B): ValidatedNel[B, A] =
    oa.fold[ValidatedNel[B, A]](Validated.invalidNel(b))(Validated.Valid(_))

  def toValidNec[B](b: => B): ValidatedNec[B, A] =
    oa.fold[ValidatedNec[B, A]](Validated.invalidNec(b))(Validated.Valid(_))

  def toRightIor[B](b: => B): Ior[B, A] = oa.fold[Ior[B, A]](Ior.Left(b))(Ior.Right(_))

  def toLeftIor[B](b: => B): Ior[A, B] = oa.fold[Ior[A, B]](Ior.Right(b))(Ior.Left(_))

  def orEmpty(implicit A: Monoid[A]): A = oa.getOrElse(A.empty)

  def liftTo[F[_]]: LiftToPartiallyApplied[F, A] = new LiftToPartiallyApplied(oa)

  def toOptionT[F[_]: Applicative]: OptionT[F, A] = oa.fold(OptionT.none[F, A])(OptionT.pure(_))
}

object NullableOps {
  private[syntax]
  class LiftToPartiallyApplied[F[_], A](oa: Nullable[A]) {
    def apply[E](ifEmpty: => E)(implicit F: ApplicativeError[F, _ >: E]): F[A] =
      liftFromNullable(oa, ifEmpty)
  }

  private
  def liftFromNullable[F[_], E, A](oa: Nullable[A], ifEmpty: => E)(implicit F: ApplicativeError[F, _ >: E]): F[A] =
    oa match {
      case NonNull(a) => F.pure(a)
      case Null    => F.raiseError(ifEmpty)
    }
}
