package com.thaj.safe.string.interpolator

// No need of annotation macros
final case class Secret(value: String) extends AnyVal

object Secret {
  implicit def secretString[A]: Safe[Secret] =
    _ => "*****"
}
