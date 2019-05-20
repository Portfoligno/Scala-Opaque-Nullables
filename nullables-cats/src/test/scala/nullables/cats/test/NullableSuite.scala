package nullables
package cats.test

import _root_.cats._
import _root_.cats.data.EitherT
import _root_.cats.laws.discipline._
import _root_.cats.laws.discipline.SemigroupalTests.Isomorphisms
import _root_.cats.laws.discipline.arbitrary._
import _root_.cats.laws.{ApplicativeLaws, CoflatMapLaws, FlatMapLaws, MonadLaws}
import _root_.cats.tests.CatsSuite

class NullableSuite extends CatsSuite {
  import nullables.cats.test.arbitrary._
  import nullables.cats.instances.nullable._
  import nullables.cats.syntax.nullable._


  implicit val isomorphisms = implicitly[Isomorphisms[Nullable]]
  implicit val eq = Eq[EitherT[Nullable, Unit, Int]]

  checkAll("Nullable[Int]", SemigroupalTests[Nullable].semigroupal[Int, Int, Int])
  checkAll("Semigroupal[Nullable]", SerializableTests.serializable(Semigroupal[Nullable]))

  checkAll("Nullable[Int]", CoflatMapTests[Nullable].coflatMap[Int, Int, Int])
  checkAll("CoflatMap[Nullable]", SerializableTests.serializable(CoflatMap[Nullable]))

  checkAll("Nullable[Int]", AlternativeTests[Nullable].alternative[Int, Int, Int])
  checkAll("Alternative[Nullable]", SerializableTests.serializable(Alternative[Nullable]))

  checkAll("Nullable[Int]", CommutativeMonadTests[Nullable].commutativeMonad[Int, Int, Int])
  checkAll("CommutativeMonad[Nullable]", SerializableTests.serializable(CommutativeMonad[Nullable]))

  checkAll("Nullable[Int] with Nullable", TraverseTests[Nullable].traverse[Int, Int, Int, Int, Nullable, Nullable])
  checkAll("Traverse[Nullable]", SerializableTests.serializable(Traverse[Nullable]))

  checkAll("Nullable[Int] with Nullable", TraverseFilterTests[Nullable].traverseFilter[Int, Int, Int])
  checkAll("TraverseFilter[Nullable]", SerializableTests.serializable(TraverseFilter[Nullable]))

  checkAll("Nullable with Unit", MonadErrorTests[Nullable, Unit].monadError[Int, Int, Int])
  checkAll("MonadError[Nullable, Unit]", SerializableTests.serializable(MonadError[Nullable, Unit]))

  test("show") {
    `null`[Int].show should ===("Null")
    1.nonNull.show should ===("NonNull(1)")

    forAll { fs: Nullable[String] =>
      fs.show should ===(fs match {
        case Null => "Null"
        case _ => s"NonNull($fs)"
      })
    }
  }

  // The following tests check laws which are a different formulation of
  // laws that are checked. Since these laws are more or less duplicates of
  // existing laws, we don't check them for all types that have the relevant
  // instances.

  test("Kleisli associativity") {
    forAll { (l: Long, f: Long => Nullable[Int], g: Int => Nullable[Char], h: Char => Nullable[String]) =>
      val isEq = FlatMapLaws[Nullable].kleisliAssociativity(f, g, h, l)
      isEq.lhs should ===(isEq.rhs)
    }
  }

  test("Cokleisli associativity") {
    forAll { (l: Nullable[Long], f: Nullable[Long] => Int, g: Nullable[Int] => Char, h: Nullable[Char] => String) =>
      val isEq = CoflatMapLaws[Nullable].cokleisliAssociativity(f, g, h, l)
      isEq.lhs should ===(isEq.rhs)
    }
  }

  test("applicative composition") {
    forAll { (fa: Nullable[Int], fab: Nullable[Int => Long], fbc: Nullable[Long => Char]) =>
      val isEq = ApplicativeLaws[Nullable].applicativeComposition(fa, fab, fbc)
      isEq.lhs should ===(isEq.rhs)
    }
  }

  val monadLaws = MonadLaws[Nullable]

  test("Kleisli left identity") {
    forAll { (a: Int, f: Int => Nullable[Long]) =>
      val isEq = monadLaws.kleisliLeftIdentity(a, f)
      isEq.lhs should ===(isEq.rhs)
    }
  }

  test("Kleisli right identity") {
    forAll { (a: Int, f: Int => Nullable[Long]) =>
      val isEq = monadLaws.kleisliRightIdentity(a, f)
      isEq.lhs should ===(isEq.rhs)
    }
  }

  // NullableIdOps tests

  test(".nonNull with null argument still results in NonNull") {
    val s: String = null
    s.nonNull should === (NonNull(null))
  }

  test("map2Eval is lazy") {
    val bomb: Eval[Nullable[Int]] = Later(sys.error("boom"))
    `null`[Int].map2Eval(bomb)(_ + _).value should ===(Null)
  }

  test("toOptionT consistency") {
    List(false) should ===(1.nonNull.toOptionT[List].isEmpty)
    List(true) should ===(Nullable.empty[Int].toOptionT[List].isEmpty)
  }
}
