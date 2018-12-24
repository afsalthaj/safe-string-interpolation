package com.thaj.safe.string.interpolator

trait Safe[A] {
  def value(a: A): String
  def hiddenLength: Int
}

object Safe {
  def apply[T](implicit ev: Safe[T]): Safe[T] = ev

  def instance[A](f: A => String, length: Int): Safe[A] =
    new Safe[A] {
      def value(a: A): String = f(a)
      val hiddenLength: Int = length
    }

  implicit val safeString: Safe[String] =
    instance(identity[String], 10)

  implicit val safeByte: Safe[Byte] =
    instance(_.toString, 3)

  implicit val safeShort: Safe[Short] =
    instance(_.toString, 5)

  implicit val safeInt: Safe[Int] =
    instance(_.toString, 10)

  implicit val safeLong: Safe[Long] =
    instance(_.toString, 10)

  implicit val safeBigInt: Safe[BigInt] =
    instance(_.toString, 10)

  implicit val safeFloat: Safe[Float] =
    instance(_.toString, 10)

  implicit val safeDouble: Safe[Double] =
    instance(_.toString, 10)

  implicit val safeBigDecimal: Safe[BigDecimal] =
    instance(_.toString, 10)

  implicit val safeChar: Safe[Char] =
    instance(_.toString, 1)

  implicit val safeBoolean: Safe[Boolean] =
    instance(_.toString, 5)

  implicit def safeOption[A: Safe]: Safe[Option[A]] =
    instance(_.fold("")(_.toString), Safe[A].hiddenLength)
}
