package test.com.thaj.safe.string.interpolator

import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.Secret
import org.specs2.{ScalaCheck, Specification}

object SafeStringSpec extends Specification with ScalaCheck {
  def is =
    s2"""
       SafeString works if and only if all interpolations are either string or case class without calling toString on to it $test
       SafeString hides if the fields are Secrets $testSecrets
      """

  final case class Dummy(name: String, age: Int)

  final case class DummyWithSecret(name: String,  secret: Secret)

  private def test = prop { (a: String, b: String, c: Int, d: Int) => {
    val res: String = (c + d).toString
    val dummy = Dummy(a, d)
    safeString"the safe string is, ${a}, ${b}, ${res}, $dummy".string must_===
      s"the safe string is, $a, ${b.toString}, $res, { age: ${dummy.age.toString}, name: ${dummy.name} }"
  }}

  private def testSecrets = prop { (a: String, b: String) => {
    val dummy = DummyWithSecret(a, Secret(b))
    safeString"the safe string with password, ${a}, $dummy".string must_===
      s"the safe string with password, $a, { secret: ${List.fill(b.toString.length)("*").mkString}, name: ${dummy.name} }"
  }}
}
