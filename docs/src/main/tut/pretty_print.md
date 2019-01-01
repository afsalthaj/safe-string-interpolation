---
layout: docs
title:  "Typesafe Pretty Print"
section: "main_menu"
position: 3
---

## Typesafe pretty-print of case-class.

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

## Why case-classes ?

While the purpose of `safe-string-interpolation` is to make sure you are passing only Strings to `safeStr`, it works for case-class instances as well.
There is a reason for this.

Delegating the job of stringifying a case class to the user has always been an infamous problem and it kills the user's time.
The `safe-string-interpolation` takes up this tedious job, and macros under the hood converts it to a readable string, while hiding `Secret` types.

PS: In the next release, we may ask the user to do `.asStr` explicitly on case classes as well. This will bring in more consistency.

```scala

@ import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.SafeString._

@ case class Test(list: List[String])
defined class Test

@ val test = Test(List("foo", "bar"))
test: Test = Test(List("foo", "bar"))

@ safeStr"test will work $test"
res4: com.thaj.safe.string.interpolator.SafeString = SafeString("test will work { list: foo,bar }")

@ val test = List("foo", "bar")
test: List[String] = List("foo", "bar")

@ safeStr"test will not work $test"
cmd6.sc:1: The provided type isn't a string nor it's a case class, or you might have tried a `toString` on non-strings !
val res6 = safeStr"test will not work $test"
                                       ^
Compilation Failed

@ safeStr"test will work by telling the compiler, yes, it is a string ${test.toString}"
cmd6.sc:1: Identified `toString` being called on the types. Either remove it or use <yourType>.asStr if it has an instance of Safe.
val res6 = safeStr"test will work by telling the compiler, yes, it is a string ${test.toString}"
                                                                                      ^
Compilation Failed

@ safeStr"test will work by telling the compiler, yes, it is a string ${test.asStr}"
res6: com.thaj.safe.string.interpolator.SafeString = SafeString("test will work by telling the compiler, yes, it is a string foo,bar")

```
