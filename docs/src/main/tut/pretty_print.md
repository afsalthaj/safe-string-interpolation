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

As mentioned in the simple example, `safeStr` can take **_ONLY_** **strings** and **case class instances**. 
Infact, the purpose of `safe-string-interpolation` is to make sure you are passing only Strings to `safeStr`.
Do remember that, if it isn't a case class passed to `safeStr`, there will not be any automatic conversion of non-string types to string types through any automatic `Safe` instances in scope. 
This is intentional. You have to explicitly convert your non-case-class types to string using `nonString.asStr`.
 
#### Case class isn't a string ! So why should it work with safeStr ? 
Delegating the job of stringifying a case class to the user has always been troublesome and it kills the user's time. In fact, it is a popular problem that we solved here.
The `safe-string-interpolation` takes up this tedious job, and macros under the hood converts it to a readable string, while hiding `Secret` types.
As mentioned earlier, anything else other than strings and (automatically stringifiable) case classes will be rejected by compiler.

```scala

@ case class Test(list: List[String])
defined class Test

@ val test = Test(List("foo", "bar"))
test: Test = Test(List("foo", "bar"))

@ safeStr"test will work $test"
res14: com.thaj.safe.string.interpolator.SafeString = SafeString("test will work { list: foo,bar }")

@ val test = List("foo", "bar")
test: List[String] = List("foo", "bar")

@ safeStr"test will not work $test"
cmd16.sc:1: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings !
val res16 = safeStr"test will not work $test"
                                        ^
Compilation Failed

@ safeStr"test will work by telling the compiler, yes, it is a string ${test.asStr}"
res16: com.thaj.safe.string.interpolator.SafeString = SafeString("test will work by telling the compiler, yes, it is a string foo,bar") 

```
