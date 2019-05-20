package nullables
package cats.test

import org.scalacheck.{Arbitrary, Cogen}

object arbitrary {
  implicit def nullableCatsArbitraryForNullable[A](implicit F: Arbitrary[Option[A]]): Arbitrary[Nullable[A]] =
    Arbitrary(F.arbitrary.map(Nullable.fromOption))

  implicit def nullableCatsCogenForNullable[A](implicit F: Cogen[Option[A]]): Cogen[Nullable[A]] =
    F.contramap(_.toOption)
}
