package test.com.thaj.safe.string.interpolator

import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.Secret
import org.specs2.{ScalaCheck, Specification}
import scalaz.{@@, NonEmptyList, Tag}
import test.com.thaj.safe.string.interpolator.SafeStringSpec.Xxx.{NewTaggedType, StringTT, StringTTT, TaggedType}

object SafeStringSpec extends Specification with ScalaCheck {
  def is =

    s2"""
       SafeString works if and only if all interpolations are either string or case class without calling toString on to it $test
       SafeString hides if the fields are Secrets $testSecrets
       SafeString works even if no arguments are passed $testSafeStrWithNoHardCodedStrings
       SafeString works for hardcoded string $testWithOnlyHardCodedString
       SafeString works for nested case classes $tesNestedCaseclass
       SafeString append works nicely without any explicit type specification for strings that are created dynamically $testSafeStringAppend
       SafeString works for GADTs $testGADT
       SafeString works for maps in case class $testMap
       SafeString works for case class with map and nonemptylist $testMapWithNonEmptyList
       SafeString works for case class with tagged type in it $testMultipleTaggedType
      """

  final case class Dummy(name: String, age: Int)

  final case class DummyWithSecret[A](name: String, secret: Secret)

  final case class NestedDummy[A](name: String, secret: Secret, dummy: Dummy)

  private def test =
    prop { (a: String, b: String, c: Int, d: Int) =>
      val res: String = (c + d).toString
      val dummy = Dummy(a, d)

      safeStr"the safe string is, ${a}, ${b}, ${res}, $dummy".string must_===
        s"the safe string is, $a, ${b.toString}, $res, { age: ${dummy.age.toString}, name: ${dummy.name} }"
    }

  private def testSecrets =
    prop { (a: String, b: String) =>
      val dummy = DummyWithSecret(a, Secret(b))

      safeStr"the safe string with password, ${a}, $dummy".string must_===
        s"the safe string with password, $a, { secret: *****, name: ${a} }"
    }

  private def tesNestedCaseclass =
    prop { (a: String, b: String, c: Int) =>
      val dummy = Dummy(a, c)
      val nestDummy = NestedDummy(a, Secret(b), dummy)

      safeStr"the safe string with password, ${a}, $nestDummy".string must_===
        s"the safe string with password, $a, { dummy: { name : $a, age : $c }, secret: *****, name: ${dummy.name} }"
    }

  private def testSafeStrWithNoHardCodedStrings =
    prop { a: String =>
      safeStr"$a".string must_=== a
    }

  private def testWithOnlyHardCodedString =
    (safeStr"somevalue".string must_=== "somevalue") and (safeStr"".string must_=== "")

  private def testSafeStringAppend = {
    val result =
      for {
        a <- Some("foo")
        b <- Some("bar")
        safe = safeStr"${a}" + safeStr"${b}"
      } yield safe

    result must beSome(safeStr"foobar")
  }

  // NonEmptyList is case class NonEmptyList[A](...)
  final case class TestGadt(list: NonEmptyList[String])

  private def testGADT =
    prop { a: String =>
      safeStr"works for gadt ${TestGadt(NonEmptyList(a, a))}".string must_=== s"works for gadt { list: ${a},${a} }"
    }

  final case class TestMap(map: Map[String, String])

  private def testMap =
    prop { a: String =>
      safeStr"works for gadt ${TestMap(Map(a -> a))}".string must_=== s"works for gadt { map: ${a} -> ${a} }"
    }

  final case class TestMapWithNonEmptyList(mapN: Map[String, NonEmptyList[String]])

  private def testMapWithNonEmptyList =
    prop { a: String =>
      safeStr"works for gadt ${TestMapWithNonEmptyList(Map(a -> NonEmptyList(a, a)))}".string must_=== s"works for gadt { mapN: ${a} -> ${a},${a} }"
    }

  final case class CaseTag(d: StringTTT, f: StringTT)

  object Xxx {

    sealed trait TaggedType
    type StringTT = String @@ TaggedType

    sealed trait NewTaggedType
    type StringTTT = String @@ NewTaggedType
  }

  private def testMultipleTaggedType =
    prop { a: String =>
      safeStr"works for taggedtypes ${CaseTag(Tag[String, NewTaggedType](a), Tag[String, TaggedType](a))}".string must_=== s"works for taggedtypes { f: ${a}, d: ${a} }"
    }
}
