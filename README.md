# safe-string-interpolation

* Being able to pass anything on to scala string interpolations might have messed up  your logs, exposed your secrets, and what not! I know you hate it.

* We may also forget stringifying domain objects when using scala string interpolations, but we hate manually creating them.

* Sometimes we rely on `scalaz.Show/cats.Show` instances on companion objects of your case classes that contributes to making functional scala code non-ubiquitous in nature.

* One simplification we did so far is to have automatic show instances (may be using shapeless), and guessing password-like fields and replacing it with "*****". Hmmm... Not anymore !

Just use 

```scala

import SafeString._

val stringg: SafeString = 
  safeStr"This is safer, guranteed and its all compile time, but pass $onlyString, and $onlyCaseClass and nothing else"`
  
```  

`safeStr` returns a `SafeString` which your logger interfaces (an example below) can then accept !

```scala
trait Loggers[F[_], E] {
  def info: SafeString => F[Unit]
  def error: SafeString => F[Unit]
  def debug: SafeString => F[Unit]

```

**Everything here is compile time. !**

## Secrets

Easy. Just wrap your any secret field anywhere with `Secret.apply`. More examples to follow

## Simple Example (just to give an intro )
```scala

scala> val a: String = "ghi"
a: String = ghi

scala> val b: String = "xyz"
b: String = xyz

scala> val c: Int = 1
c: Int = 1

scala> // safeStr interpolation

scala> safeStr"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c}"
<console>:24: error: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings!
       safeStr"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c}"
                                                                                                    ^
scala> safeStr"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}"
res2: com.thaj.safe.string.interpolator.SafeString = SafeString(The scala string interpol can be a bit dangerous with your secrets. ghi, xyz)

```

## Case class Example

```scala

scala> case class Dummy(name: String, age: Int)
defined class Dummy

scala> val dummy = Dummy("Afsal", 1)
dummy: Dummy = Dummy(Afsal,1)

scala> val a: String = "realstring"
a: String = realstring

scala> safeStr"This is safer ! ${a} : ${dummy}"
res3: com.thaj.safe.string.interpolator.SafeString = SafeString(This is safer ! realstring : { age: 1, name: Afsal })

```


## Can't Cheat!

```scala

scala> safeStr"I am going to call a toString on a case class to satisfy compiler ! ${a} : ${dummy.toString}"
<console>:23: error: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings!
       safeStr"I am going to call a toString on a case class to satisfy compiler ! ${a} : ${dummy.toString}"
                                                 ^

```

safe-string-interpolator hates it when you do `toString` on non-string types. Instead, you can use `yourType.asStr` 
and safe-string-interpolator will ensure it is safe to convert it to String. 

i.e,

```scala

val a: String = "afsal"
val b: String = "john"
val c: Int = 1

scala> safeStr"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c.toString}"
<console>:24: error: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings!
       
scala> safeStr"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c.asStr}"  
// Compiles sucess 


```

PS: An only issue with this tight approach to being safe is that sometimes you may need to end up doing `thisIsADynamicString.asStr`, and that's more of a failed
fight with scala type inference.


## How about secrets ?

```scala
scala> import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.SafeString._

scala> import com.thaj.safe.string.interpolator.Secret
import com.thaj.safe.string.interpolator.Secret

scala> val conn = DbConnection("posgr", Secret("this will be hidden"))
conn: DbConnection = DbConnection(posgr,Secret(this will be hidden))

scala> safeStr"the db conn is $conn"
res0: com.thaj.safe.string.interpolator.SafeString = SafeString(the db conn is { password: *******************, name: posgr })

```

## Your own secret ?

If you don't want to use `interpolation.Secret` data type and need to use your own, then define `Safe` instance for it.

```scala
case class MySecret(value: String) extends AnyVal

implicit val safeMySec: Safe[MySecret] = _ => "****"

val conn = DbConnection("posgr", MySecret("this will be hidden"))


scala> safeStr"the db is $conn"
res1: com.thaj.safe.string.interpolator.SafeString = SafeString(the db is { password: ****, name: posgr })

```
