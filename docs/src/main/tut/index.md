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

Easy ! Use `safeStr"your log $a $b, $c"` instead of `s"your log $a $b, $c"` !

Add this in your build.sbt

```scala
libraryDependencies += "io.github.afsalthaj" %% "safe-string" % "1.2.7"
```

Or, in ammonite;

```scala
@ import $ivy.`io.github.afsalthaj::safe-string:1.2.7`
import $ivy.$
```

```scala


scala> import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.SafeString._

scala> case class X(name: String)
defined class X

scala> val caseClassInstance = X("foo")
caseClassInstance: X = X(foo)

scala> val onlyString: String = "bar"
onlyString: String = bar

scala> safeStr"Works only if its either a string or a case class instance $caseClassInstance or $onlyString"
res0: com.thaj.safe.string.interpolator.SafeString = SafeString(Works only if its either a string or a case class instance { name: foo } or bar)

scala> class C
defined class C

scala> val nonCaseClass = new C
nonCaseClass: C = C@7e3131c8

scala> safeStr"Doesn't work if there is a non-case class $nonCaseClass or $onlyString"
<console>:17: error: The provided type is neither a string nor a case-class. Consider converting it to strings using <value>.asStr.
       safeStr"Doesn't work if there is a non-case class $nonCaseClass or $onlyString"
                                                          ^
// And don't cheat by `toString`
scala> safeStr"Doesn't work if there is a non-case class ${nonCaseClass.toString} or $onlyString"
<console>:17: error: Identified `toString` being called on the types. Either remove it or use <yourType>.asStr if it has an instance of Safe.
       safeStr"Doesn't work if there is a non-case class ${nonCaseClass.toString} or $onlyString"
                                                                        ^

```

# Concept and example usages.

`safeStr""` is just like `s""` in scala, but it is type safe and _allows only_ 

* **strings**.
* **case classes** which will be converted to json-like string by inspecting all fields, be it deeply nested or not, at compile time.
* and provides consistent way to **hide secrets**.

To understand more on the concepts and usages, please go through:

1)  [A Simple Example](https://afsalthaj.github.io/safe-string-interpolation/examples.html)
2) [Typesafe Pretty prints](https://afsalthaj.github.io/safe-string-interpolation/pretty_print.html) and 
3) [Logging Secrets / Passwords](https://afsalthaj.github.io/safe-string-interpolation/secrets.html) 

to get started !


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