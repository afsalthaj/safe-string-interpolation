package com.thaj.safe.string.interpolator

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

final case class Field[T](name: String, value: T) {
  override def toString: String = s"""$name: $value"""
}

object Field {
  def asString[T](list: Set[Field[T]]): String =
    s"{ ${list.map(t => t.toString).mkString(", ")} }"
}

final case class SafeString private(string: String) extends AnyVal {
  def +(that: SafeString) = SafeString(this.string ++ that.string)
}

object SafeString {

  implicit class SafeStringContext(val sc: StringContext) {
    def safeString(args: Any*): SafeString = macro Macro.impl
  }


  object Macro {
    def impl(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[SafeString] = {
      import c.universe.{Name => _, _}

      object CaseClassFieldAndName {
        def unapply(sym: TermSymbol): Option[(TermName, Type)] = {
          if (sym.isCaseAccessor && sym.isVal)
            Some((newTermName(sym.name.toString.trim), sym.typeSignature))
          else
            None
        }
      }

      c.prefix.tree match {

        case Apply(_, List(Apply(_, rawParts))) =>
          val parts: Seq[String] = rawParts map { case Literal(Constant(const: String)) => const }

          val res: c.universe.Tree =
            args.toList.foldLeft(q"""StringContext.apply(..${parts})""")({ (acc, t) => {

              val nextElement = t.tree
              val tag = c.WeakTypeTag(nextElement.tpe)
              val symbol = tag.tpe.typeSymbol

              if (tag.tpe != typeOf[String] && symbol.isClass && symbol.asClass.isCaseClass) {
                val r: Set[c.universe.Tree] =
                  nextElement.tpe.members.collect {
                    case CaseClassFieldAndName(nme, typ) => {
                      // Fix the toString here
                      q"""com.thaj.safe.string.interpolator.Field(${nme.toString}, $nextElement.$nme.toString)"""
                    }
                  }.toSet

                val field = q"""com.thaj.safe.string.interpolator.Field.asString($r)"""

                acc match {
                  case q"""StringContext.apply(..$raw).s(..$previousElements)""" => q"""StringContext.apply(..$raw).s(($previousElements :+ ..${field}) :_*)"""
                  case _ => q"""${acc}.s(..$field)"""
                }
              }

              else if (tag.tpe == typeOf[String]) {
                acc match {
                  case q"""StringContext.apply(..$raw).s(..$previousElements)""" => q"""StringContext.apply(..$raw).s(($previousElements :+ $nextElement) :_*)"""
                  case _ => q"""${acc}.s($nextElement)"""
                }
              } else {
                c.abort(t.tree.pos, "The provided type isn't a string nor it's a case class, or you might have tried a `toString` on something while using `safeString`")
              }
            }
            })

          c.Expr(q"""com.thaj.safe.string.interpolator.SafeString($res)""")

        case _ =>
          c.abort(c.prefix.tree.pos, "bla")

      }
    }
  }

}
