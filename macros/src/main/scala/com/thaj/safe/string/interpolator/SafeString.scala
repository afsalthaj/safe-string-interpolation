package com.thaj.safe.string.interpolator

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

final case class Field[T : Safe](name: String, value: T) {
  override def toString: String = s"""$name: ${Safe[T].value(value)}"""
}

final case class SafeString private(string: String) extends AnyVal {
  def +(that: SafeString) = SafeString(this.string ++ that.string)
}

object SafeString {

  implicit class SafeStringContext(val sc: StringContext) {
    def safeStr(args: Any*): SafeString = macro Macro.impl
  }

  implicit class AsString[T: Safe](s: => T) {
    def asStr: String = Safe[T].value(s)
  }

  object Macro {
    // Not public, just for macros
    def jsonLike(list: Set[String]) =
      s"{ ${list.mkString(", ")} }"

    def impl(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[SafeString] = {
      import c.universe.{Name => _, _}

      object CaseClassFieldAndName {
        def unapply(sym: TermSymbol): Option[(TermName, Type)] = {
          if (sym.isCaseAccessor && sym.isVal)
            Some((TermName(sym.name.toString.trim), sym.typeSignature))
          else
            None
        }
      }

      c.prefix.tree match {

        case Apply(_, List(Apply(_, partz))) =>
          val parts: Seq[String] = partz map { case Literal(Constant(const: String)) => const }

          val res: c.universe.Tree =
            args.toList.foldLeft(q"""StringContext.apply(..${parts})""")({ (acc, t) => {

              val nextElement = t.tree

              val tag = c.WeakTypeTag(nextElement.tpe)
              val symbol = tag.tpe.typeSymbol

              if(nextElement.toString().contains(".toString"))
                c.abort(t.tree.pos, s"Identified `toString` being called on the types. Either remove it or use <yourType>.asStr if it has an instance of Safe.")

              if (!(tag.tpe =:= typeOf[String]) && symbol.isClass && symbol.asClass.isCaseClass) {
                val r: Set[c.universe.Tree] =
                  nextElement.tpe.members.collect {
                    case CaseClassFieldAndName(nme, typ) =>
                      q"""com.thaj.safe.string.interpolator.Field(${nme.toString}, $nextElement.$nme)"""
                  }.toSet

                val field = q"""com.thaj.safe.string.interpolator.SafeString.Macro.jsonLike($r.map(_.toString))"""

                acc match {
                  case q"""StringContext.apply(..$raw).s(..$previousElements)""" => q"""StringContext.apply(..$raw).s(($previousElements :+ ..${field}) :_*)"""
                  case _ => q"""${acc}.s(..$field)"""
                }
              }

              else if (tag.tpe =:= typeOf[String]) {
                acc match {
                  case q"""StringContext.apply(..$raw).s(..$previousElements)""" => q"""StringContext.apply(..$raw).s(($previousElements :+ $nextElement) :_*)"""
                  case _ => q"""${acc}.s($nextElement)"""
                }
              } else {
                c.abort(t.tree.pos, "The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings !")
              }
            }
            })

          res match {
            case q"""StringContext.apply(..$raw).s(..$previousElements)""" => c.Expr(q"""com.thaj.safe.string.interpolator.SafeString($res)""")
            case q"""StringContext.apply($raw)"""                          => c.Expr(q"""com.thaj.safe.string.interpolator.SafeString($raw)""")
          }

        case _ =>
          c.abort(c.prefix.tree.pos, "The pattern can't be used with the safeStr interpolation.")

      }
    }
  }
}
