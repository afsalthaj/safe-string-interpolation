# safe-string-interpolation

The string interpolation and being able to pass anything on to it might have messed up 
your logs, exposed your secrets, and what not! I know you hate it.

And I know you are done with forgetting stringifying domain objects when using scala string interpolations.
At the same I know you hate manually creating its string/json representations.

A few us also rely `scalaz.Show/cats.Show` instances of companion objects of your case classes that contributes to the code that kills the powerful functional scala code non-ubiquitous in nature..
One simolification we did was or to make  shapeless way of creating show instances for your case classes. 
Ah, hmm ! Not anymore. This is one step ahead !


Just use 
import SafeString._

val stringg: SafeString = 
  `safeString"This is safer, guranteed and all compile time and pass $onyString, $onlyCaseClass and nothing else"` !

And ofcourse, you guessed it right. 
It `safeString` returns a `SafeString` which your finally tagless logger interfaces can accept, and take a deep breath. Compiler will take care of the rest.


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

scala>


```



