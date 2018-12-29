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
  
We need more type driven logging, consistent secret management, catching erroneous `toStrings` at compile time, rather than getting shocked and surprised at runtime.  
 
# Solution

Add this in your build.sbt

```scala
libraryDependencies += "io.github.afsalthaj" %% "safe-string" % "1.2.0"
```

Or, in ammonite;

```scala
@ import $ivy.`io.github.afsalthaj::safe-string:1.2.0`
import $ivy.$
```

```scala

// Ammonite

@ import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.SafeString._

@ case class X(name: String)
defined class X

@ val caseClassInstance = X("foo")
caseClassInstance: X = X("foo")

@ val onlyString: String = "bar"
onlyString: String = "bar"

@ safeStr"This is type safe logging works only if it is either a string or a case class instance $caseClassInstance or $onlyString"
res8: com.thaj.safe.string.interpolator.SafeString = SafeString("This is type safe logging works only if it is either a string or a case class instance { name: foo } or bar")

@ class C
defined class C

@ val nonCaseClass = new C
nonCaseClass: C = ammonite.$sess.cmd9$C@4fd92289

@ safeStr"This is type safe logging works only if it is either a string or a case class instance $nonCaseClass or $onlyString"
cmd11.sc:1: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings !
val res11 = safeStr"This is type safe logging works only if it is either a string or a case class instance $nonCaseClass or $onlyString"
                                                                                                            ^
Compilation Failed

```

# Concept and example usages.

`safeStr""` is just like `s""` in scala, but it is type safe and _allows only_ 

* **strings**.
* **case classes** which will be converted to json-like string by inspecting all fields, be it deeply nested or not, at compile time.
* and provides consistent way to **hide secrets**.

To understand more on the concepts and usages, please go through:

* [A Simple Example](https://afsalthaj.github.io/safe-string-interpolation/examples.html)
* [Typesafe Pretty prints](https://afsalthaj.github.io/safe-string-interpolation/pretty_print.html) and 
* [Logging Secrets / Passwords](https://afsalthaj.github.io/safe-string-interpolation/secrets.html) to get started !


# Add this in your logs !

`safeStr` returns a `SafeString` which your logger interfaces (an example below) can then accept !


```scala
trait Loggers[F[_], E] {
  def info: SafeString => F[Unit]
  def error: SafeString => F[Unit]
  def debug: SafeString => F[Unit]

```

**Everything here is compile time. !** 


----------------------------------------

Our application isn’t resilient if it lacks human-readable logs and doesn’t manage secret variables consistently. Moreover, it said to be maintainable only when it is type driven and possess more compile time behaviour, in this context, be able to fail a build/compile when someone does a `toString` in places where you shouldn’t. Hope it helps !