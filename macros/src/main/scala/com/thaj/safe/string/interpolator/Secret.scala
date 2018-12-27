package com.thaj.safe.string.interpolator

// No need of annotation macros
final case class Secret(value: String) extends AnyVal

object Secret {
  implicit val secretString: Safe[Secret] =
    (a: Secret) => List.fill(a.value.length)("*").mkString
}
