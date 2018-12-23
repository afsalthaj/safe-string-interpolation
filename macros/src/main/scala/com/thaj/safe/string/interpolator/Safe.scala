package com.thaj.safe.string.interpolator

trait Safe[A] {
  def value(a: A): String
}

object Safe {
  def apply[T](implicit ev: Safe[T]): Safe[T] = ev

  implicit val safeString: Safe[String] =
    identity[String]

  implicit val safeInt: Safe[Int] =
    _.toString

  implicit val safeLong: Safe[Long] =
    _.toString

  implicit val safeDouble: Safe[Double] =
    _.toString

  implicit val safeChar: Safe[Char] =
    _.toString
}