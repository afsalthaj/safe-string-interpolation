# safe-string-interpolation

* Being able to pass anything on to scala string interpolations might have messed up  your logs, exposed your secrets, and what not! I know you hate it.

* We may also forget stringifying domain objects when using scala string interpolations, but we hate manually creating them.

* A few us also relied on `scalaz.Show/cats.Show` instances on companion objects of your case classes that contributes to making functional scala code non-ubiquitous in nature.

* One simplification we did so far is to have automatic show instances (may be using shapeless) for your case classes and reduced the compile time speed, and guessing password-like fields and replacing it with "*****". Hmm... Not anymore !

Just use 

```scala

import SafeString._

val stringg: SafeString = 
  safeString"This is safer, guranteed and its all compile time, but pass $onyString, and $onlyCaseClass and nothing else"`
  
```  

`safeString` returns a `SafeString` which your logger interfaces (log.info(safeString)) can then accept !

**Everything here is compile time. !**

### Soon to be added

Secret annotations for domain objects !!

### Simple Example
```scala

scala> val a: String = "ghi"
a: String = ghi

scala> val b: String = "xyz"
b: String = xyz

scala> val c: Int = 1
c: Int = 1

scala> // safeString interpolation

scala> safeString"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c}"
<console>:24: error: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on something while using `safeString`
       safeString"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${c}"
                                                                                                    ^

scala> val cString: String = c.toString
cString: String = 1

scala> safeString"The scala string interpol can be a bit dangerous with your secrets. ${a}, ${b}, ${cString}"
res2: com.thaj.safe.string.interpolator.SafeString = SafeString(The scala string interpol can be a bit dangerous with your secrets. ghi, xyz, 1)

```

### Case class Example

```scala

scala> case class Dummy(name: String, age: Int)
defined class Dummy

scala> val dummy = Dummy("Afsal", 1)
dummy: Dummy = Dummy(Afsal,1)

scala> val a: String = "realstring"
a: String = realstring

scala> safeString"This is safer ! ${a} : ${dummy}"
res3: com.thaj.safe.string.interpolator.SafeString = SafeString(This is safer ! realstring : { age: 1, name: Afsal })

```


#### Ah Dont be a bad Boy!

```scala

scala> safeString"I am going to call a toString on a case class to satisfy compiler ! ${a} : ${dummy.toString}"
<console>:23: error: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on something while using `safeString`
       safeString"I am going to call a toString on a case class to satisfy compiler ! ${a} : ${dummy.toString}"
                                                 ^

```



