---
layout: home
title:  "Home"
section: "home"
---

# Problem

Being able to pass anything on to scala string interpolations might have messed up  your logs, exposed your secrets, and what not! I know you hate it.
We may also forget stringifying domain objects when using scala string interpolations, but stringifying it manually is a tedious job. Instead we just do `toString` which sometimes can spew out object hash, your valuable secrets and in fact many useless messages.

A few terrible logging examples :

  ``` 
   INFO: The student logged in: @4f9a2c08 // object.toString
   INFO: The student logged in: Details(NameParts("john", "stephen"), "efg", "whoknowswhatiswhat"...) 
   INFO: The student logged in: scala.Map(...)
   INFO: The student logged in: Details("name", "libraryPassword!!")
  ```
Apart from logging, we have bumped into situations where we tried to concatenate / append "Any"thing using string interpolations, often resulting in logical errors as well.  
You know this, we need more type driven logging and string interpolation operations, consistent secret management, catching erroneous `toStrings` at compile time, rather than getting shocked and surprised at runtime.  
 
# Solution

Easy ! Use `ss"your log $a $b, $c"` instead of `s"your log $a $b, $c"` !

You can also use safeStr""

Add this in your build.sbt

```scala
libraryDependencies += "io.github.afsalthaj" %% "safe-string-interpolation" % "2.1.1" 
```

Or, in ammonite;

```scala
@ import $ivy.`io.github.afsalthaj::safe-string-interpolation:2.1.1`
import $ivy.$
```

```scala


scala> import com.thaj.safe.string.interpolator._, instances._
import com.thaj.safe.string.interpolator._, instances._

scala> case class X(name: String)
defined class X

scala> val caseClassInstance = X("foo")
caseClassInstance: X = X(foo)

scala> val string: String = "bar"
onlyString: String = bar

// Note that case classes and other complex types only because we imported `instances._`. Otherwise library allows you to use only primitive types with ss.
scala> ss"Works only if all of them has an instance of safe $caseClassInstance or $string"
res0: com.thaj.safe.string.interpolator.SafeString = SafeString(Works only if it all of them has an instance of safe { name : foo } or bar)

scala> class C
defined class C

scala> val nonCaseClass = new C
nonCaseClass: C = C@7e3131c8

scala> ss"Doesn't work if there is a non-case class $nonCaseClass or $string"
<console>:17: error: unable to find a safe instance for class C. MMake sure the type has safe instance. Either define Safe instance manually, or `import com.thaj.safe.string.interpolator._` to get instances for products, coproducts and other non primitive types.
                                                          ^
// And don't cheat by `toString`
scala> safeStr"Doesn't work if there is a non-case class ${nonCaseClass.toString} or $string"
<console>:17: error: Identified `toString` being called on the types. Make sure the type has a instance of Safe.
                                                                        ^
```

# Concept and example usages.

`ss""` is just like `s""` in scala, but it is type safe and _allows only_ types that has a safe instance. By default it allows only
to include primitive instances. If you want to make it work for case classes, or sealed traits and other non primitive types such as list,
option, maybe, tagged type etc, you have to explicitly import `instances._`

If you have a case class or sealed traits, the macros in `Safe.scala` will automatically derive it's safe instance. 
More on this later.

To understand more on the concepts and usages, please go through:

- [A Simple Example](https://afsalthaj.github.io/safe-string-interpolation/examples.html)

- [Case class & Sealed traits (Products & Coproducts)](https://afsalthaj.github.io/safe-string-interpolation/pretty_print.html) and 

- [Logging Secrets / Passwords](https://afsalthaj.github.io/safe-string-interpolation/secrets.html) 

to get started !


# Add this in your logs !

`safeStr` returns a `SafeString` which your logger interfaces (an example below) can then accept !


```scala
trait Loggers[F[_], E] {
  def info: SafeString => F[Unit]
  def error: SafeString => F[Unit]
  def debug: SafeString => F[Unit]
}

```

**Everything here is compile time. !** 


----------------------------------------

Our application isn’t resilient if it lacks human-readable logs and doesn’t manage secret variables consistently. Moreover, it said to be maintainable only when it is type driven and possess more compile time behaviour, in this context, be able to fail a build/compile when someone does a `toString` in places where you shouldn’t. Hope it helps !
