---
layout: docs
title:  "Typesafe Pretty Print"
section: "main_menu"
position: 3
---

## Case class Example

```scala

scala> case class Xyz(abc: Abc, name: String)
defined class Xyz

scala> val s = Xyz(Abc("a", "b", "c"), "x")
s: Xyz = Xyz(Abc(a,b,c),x)

// scala string interpolation
scala> s"The value of xyz is $s"
res0: String = The value of xyz is Xyz(Abc(a,b,c),x)

// type safe string interpolation
scala> safeStr"The value of xyz is $s"
res1: com.thaj.safe.string.interpolator.SafeString = SafeString(The value of xyz is { name: x, abc: {x : a, y : b, z : c} })

scala> res1.string
res2: The value of xyz is { name: x, abc: {x : a, y : b, z : c} }
```


This works for any level of **deep nested structure of case class**. This is done with the support of macro materializer in `Safe.scala`.
The main idea here is, if any field in any part of the nested case class isn't safe to be converted to string, it will throw a compile time.
Also, if any part of case classes has `Secret`s in it, the value will be hidden. More on this in `Secret / Password` section

## Type-safety

As mentioned in the simple example, `safeStr` can take **_ONLY_** **strings** and **case class instances** with each field in the case class having a `Safe` instance.

Don't worry, this doesn't mean you need to keep creating `Safe` instances. Macros machinaries under the hood takes care of it. 

If something goes wrong, it will be most probably, macros was unable to find an instance for `Safe` for some field in your (may be deeply nested) case class.
So just create an instance for Safe. However, we should try and avoid manual creation of `Safe` instance.

`Safe` instances are already provided for collections, primitives and scalaz.Tag types.

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