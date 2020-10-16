package com.thaj.safe.string.interpolator

trait Safe[A] {
  def value(a: A): String
}

object Safe {
  def apply[T](implicit ev: Safe[T]): Safe[T] = ev
}
