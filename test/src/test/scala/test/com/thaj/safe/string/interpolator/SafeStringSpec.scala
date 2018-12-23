package test.com.thaj.safe.string.interpolator

import com.thaj.safe.string.interpolator.SafeString._
import org.specs2.{ScalaCheck, Specification}

object SafeStringSpec extends Specification with ScalaCheck {
  def is =
    s2"""
         SafeString works if and only if all interpolations are either string or case class without calling toString on to it $test
      """

  final case class Dummy(name: String, age: Int)

  def test = prop { (a: String, b: String, c: Int, d: Int) => {
    val res: String = (c + d).toString
    val dummy = Dummy(a, d)
    safeString"the safe string is, ${a}, ${b}, ${res}, $dummy".string must_===
      s"the safe string is, $a, ${b.toString}, $res, { age: ${dummy.age.toString}, name: ${dummy.name} }"
  }}
}
