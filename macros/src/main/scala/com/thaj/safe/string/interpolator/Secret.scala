package com.thaj.safe.string.interpolator

// No need of annotation macros
final case class Secret[A](value: A) extends AnyVal

object Secret {
  implicit def secretString[A: Safe]: Safe[Secret[A]] =
    _ => "*****"
}
