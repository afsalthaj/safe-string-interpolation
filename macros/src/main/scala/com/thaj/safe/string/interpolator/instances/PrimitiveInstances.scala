package com.thaj.safe.string.interpolator.instances

import com.thaj.safe.string.interpolator.Safe

trait PrimitiveInstances extends SecretInstance {
  implicit val safeString: Safe[String] =
    identity[String]

  implicit val safeByte: Safe[Byte] =
    _.toString

  implicit val safeShort: Safe[Short] =
    _.toString

  implicit val safeInt: Safe[Int] =
    _.toString

  implicit val safeLong: Safe[Long] =
    _.toString

  implicit val safeDouble: Safe[Double] =
    _.toString

  implicit val safeFloat: Safe[Float] =
    _.toString

  implicit val safeChar: Safe[Char] =
    _.toString

  implicit val safeBoolean: Safe[Boolean] =
    _.toString

  implicit val safeBigInt: Safe[BigInt] =
    _.toString

  implicit val safeBigDecimal: Safe[BigDecimal] =
    _.toString
}
