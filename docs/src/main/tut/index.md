---
layout: home
title:  "Home"
section: "home"
---

Being able to pass anything on to scala string interpolations might have messed up  your logs, exposed your secrets, and what not! I know you hate it.
We may also forget stringifying domain objects when using scala string interpolations, but stringifying it manually is a tedious job. Instead we just do `toString` which sometimes can spew out useless string representations. 

Bad logs:

  ``` 
   INFO: The student logged in: @4f9a2c08 // object.toString
   INFO: The student logged in: Details(NameParts("john", "stephen"), "efg", "whoknowswhatiswhat"...) 
   INFO: The student logged in: scala.Map(...)
   INFO: The student logged in: Details("name", "libraryPassword!!")
  ```

Sometimes we rely on `scalaz.Show/cats.Show` instances on companion objects of case classes and then do `s"my domain object is ${domainObject.show}"`, but the creation of `show` instances has never been proved practical in larger applications. 

One simplification we did so far is to have automatic show instances (may be using shapeless), and guessing password-like fields and replacing it with "*****". 

Hmmm... Not anymore !

# Solution

`safeStr""` is just like `s""` in scala, but it is type safe and _allows only_ 

* **strings**.
* **case classes** which will be converted to json-like string by inspecting all fields, be it deeply nested or not, at compile time.
* and provides consistent way to **hide secrets**.
 
Checkout the following:

* [simple examples](https://afsalthaj.github.io/safe-string-interpolation/examples.html)
* [type safe pretty print of case classes](https://afsalthaj.github.io/safe-string-interpolation/pretty_print.html) and 
* [secret / password logging](https://afsalthaj.github.io/safe-string-interpolation/secrets.html) to get started !

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