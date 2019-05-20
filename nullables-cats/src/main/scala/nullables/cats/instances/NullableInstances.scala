package nullables
package cats.instances

import _root_.cats._

import scala.annotation.tailrec

trait NullableInstances extends nullables.cats.instances.kernel.NullableInstances {

  implicit val nullablesCatsInstancesForNullable: Traverse[Nullable]
    with MonadError[Nullable, Unit]
    with Alternative[Nullable]
    with CommutativeMonad[Nullable]
    with CoflatMap[Nullable] =
    new Traverse[Nullable] with MonadError[Nullable, Unit] with Alternative[Nullable] with CommutativeMonad[Nullable]
    with CoflatMap[Nullable] {

      def empty[A]: Nullable[A] = Null

      def combineK[A](x: Nullable[A], y: Nullable[A]): Nullable[A] = x.orElse(y)

      def pure[A](x: A): Nullable[A] = NonNull(x)

      override def map[A, B](fa: Nullable[A])(f: A => B): Nullable[B] =
        fa.map(f)

      def flatMap[A, B](fa: Nullable[A])(f: A => Nullable[B]): Nullable[B] =
        fa.flatMap(f)

      @tailrec
      def tailRecM[A, B](a: A)(f: A => Nullable[Either[A, B]]): Nullable[B] =
        f(a) match {
          case Null           => Null
          case NonNull(Left(a1)) => tailRecM(a1)(f)
          case NonNull(Right(b)) => NonNull(b)
        }

      override def map2[A, B, Z](fa: Nullable[A], fb: Nullable[B])(f: (A, B) => Z): Nullable[Z] =
        fa.flatMap(a => fb.map(b => f(a, b)))

      override def map2Eval[A, B, Z](fa: Nullable[A], fb: Eval[Nullable[B]])(f: (A, B) => Z): Eval[Nullable[Z]] =
        fa match {
          case Null    => Now(Null)
          case NonNull(a) => fb.map(_.map(f(a, _)))
        }

      def coflatMap[A, B](fa: Nullable[A])(f: Nullable[A] => B): Nullable[B] =
        if (fa.isDefined) NonNull(f(fa)) else Null

      def foldLeft[A, B](fa: Nullable[A], b: B)(f: (B, A) => B): B =
        fa match {
          case Null    => b
          case NonNull(a) => f(b, a)
        }

      def foldRight[A, B](fa: Nullable[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        fa match {
          case Null    => lb
          case NonNull(a) => f(a, lb)
        }

      def raiseError[A](e: Unit): Nullable[A] = Null

      def handleErrorWith[A](fa: Nullable[A])(f: Unit => Nullable[A]): Nullable[A] = fa.orElse(f(()))

      def traverse[G[_]: Applicative, A, B](fa: Nullable[A])(f: A => G[B]): G[Nullable[B]] =
        fa match {
          case Null    => Applicative[G].pure(Null)
          case NonNull(a) => Applicative[G].map(f(a))(NonNull(_))
        }

      override def reduceLeftToOption[A, B](fa: Nullable[A])(f: A => B)(g: (B, A) => B): Option[B] =
        fa.map(f).toOption

      override def reduceRightToOption[A, B](fa: Nullable[A])(f: A => B)(g: (A, Eval[B]) => Eval[B]): Eval[Option[B]] =
        Now(fa.map(f).toOption)

      override def reduceLeftOption[A](fa: Nullable[A])(f: (A, A) => A): Option[A] = fa.toOption

      override def reduceRightOption[A](fa: Nullable[A])(f: (A, Eval[A]) => Eval[A]): Eval[Option[A]] =
        Now(fa.toOption)

      override def minimumOption[A](fa: Nullable[A])(implicit A: Order[A]): Option[A] = fa.toOption

      override def maximumOption[A](fa: Nullable[A])(implicit A: Order[A]): Option[A] = fa.toOption

      override def get[A](fa: Nullable[A])(idx: Long): Option[A] =
        if (idx == 0L) fa.toOption else None

      override def size[A](fa: Nullable[A]): Long = fa.fold(0L)(_ => 1L)

      override def foldMap[A, B](fa: Nullable[A])(f: A => B)(implicit B: Monoid[B]): B =
        fa.fold(B.empty)(f)

      override def find[A](fa: Nullable[A])(f: A => Boolean): Option[A] =
        fa.filter(f).toOption

      override def exists[A](fa: Nullable[A])(p: A => Boolean): Boolean =
        fa.exists(p)

      override def forall[A](fa: Nullable[A])(p: A => Boolean): Boolean =
        fa.forall(p)

      override def toList[A](fa: Nullable[A]): List[A] = fa.toList

      override def filter_[A](fa: Nullable[A])(p: A => Boolean): List[A] =
        fa.filter(p).toList

      override def takeWhile_[A](fa: Nullable[A])(p: A => Boolean): List[A] =
        fa.filter(p).toList

      override def dropWhile_[A](fa: Nullable[A])(p: A => Boolean): List[A] =
        fa.filterNot(p).toList

      override def isEmpty[A](fa: Nullable[A]): Boolean =
        fa.isEmpty

      override def collectFirst[A, B](fa: Nullable[A])(pf: PartialFunction[A, B]): Option[B] = fa.collectFirst(pf)

      override def collectFirstSome[A, B](fa: Nullable[A])(f: A => Option[B]): Option[B] = fa.fold(None: Option[B])(f)
    }

  implicit def nullablesCatsShowForNullable[A](implicit A: Show[A]): Show[Nullable[A]] =
    new Show[Nullable[A]] {
      def show(fa: Nullable[A]): String = fa match {
        case NonNull(a) => s"NonNull(${A.show(a)})"
        case Null    => "Null"
      }
    }

  implicit val nullablesCatsTraverseFilterForNullable: TraverseFilter[Nullable] = new TraverseFilter[Nullable] {
    val traverse: Traverse[Nullable] = nullables.cats.instances.nullable.nullablesCatsInstancesForNullable

    override def mapFilter[A, B](fa: Nullable[A])(f: A => Option[B]): Nullable[B] =
      fa.flatMap(a => Nullable.fromOption(f(a)))

    override def filter[A](fa: Nullable[A])(f: A => Boolean): Nullable[A] = fa.filter(f)

    override def collect[A, B](fa: Nullable[A])(f: PartialFunction[A, B]): Nullable[B] = fa.collect(f)

    override def flattenOption[A](fa: Nullable[Option[A]]): Nullable[A] = fa.flatMap(Nullable.fromOption)

    def traverseFilter[G[_], A, B](fa: Nullable[A])(f: A => G[Option[B]])(implicit G: Applicative[G]): G[Nullable[B]] =
      fa match {
        case Null    => G.pure(Nullable.empty[B])
        case NonNull(a) => G.map(f(a))(Nullable.fromOption)
      }

    override def filterA[G[_], A](fa: Nullable[A])(f: A => G[Boolean])(implicit G: Applicative[G]): G[Nullable[A]] =
      fa match {
        case Null    => G.pure(Nullable.empty[A])
        case NonNull(a) => G.map(f(a))(b => if (b) NonNull(a) else Null)
      }

  }
}
