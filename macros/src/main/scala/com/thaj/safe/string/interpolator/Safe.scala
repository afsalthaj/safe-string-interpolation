package com.thaj.safe.string.interpolator

import scalaz.{@@, NonEmptyList}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox


trait Safe[A] {
  def value(a: A): String
}

object Safe {
  def apply[T](implicit ev: Safe[T]): Safe[T] = ev

  implicit def materializeMappable[T]: Safe[T] = macro materializeSafe[T]

  def materializeSafe[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Safe[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    val str =
      fields.foldLeft(Set[(TermName, c.universe.Tree)]()) { (str, field) ⇒
        val tag = c.WeakTypeTag(field.typeSignature)
        val symbol = tag.tpe.typeSymbol

        val fieldName = field.name.toTermName
        str ++ Set((fieldName, q""" com.thaj.safe.string.interpolator.Safe[$symbol]"""))
      }

    val res =
      q"""new Safe[$tpe] {
         override def value(a: $tpe): String = "{" + ${str.map{case(x, y) => q""" ${x.toString} + " : " + $y.value(a.$x) """ }}.mkString(", ") + "}"
      }"""

    c.Expr[Safe[T]] { res }
  }

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

  implicit val safeBigInt: Safe[BigInt] =
    _.toString

  implicit def safeNonEmptyList[A: Safe]: Safe[NonEmptyList[A]] =
    _.map(t => Safe[A].value(t)).list.toList.mkString(",")

  implicit def safeTagged[A: Safe, T]: Safe[A @@ T] =
    a => Safe[A].value(scalaz.Tag.unwrap(a))

  implicit def safeList[A: Safe]: Safe[List[A]] =
    a => a.map(t => Safe[A].value(t)).mkString(",")

  implicit def safeSet[A: Safe]: Safe[Set[A]] =
    a => a.map(t => Safe[A].value(t)).mkString(",")

  implicit def safeMap[A: Safe, B: Safe]: Safe[Map[A, B]] =
    a => a.map { case (aa, bb) => (Safe[A].value(aa), Safe[B].value(bb)) }.mkString(",")
}