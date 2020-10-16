---
layout: docs
title:  "Products and Coproducts"
section: "main_menu"
position: 3
---

## Typesafe pretty-print of Products (case class)

```scala

scala> case class Xyz(abc: Abc, name: String)
defined class Xyz

scala> val s = Xyz(Abc("a", "b", "c"), "x")
s: Xyz = Xyz(Abc(a,b,c),x)

// scala string interpolation
scala> s"The value of xyz is $s"
res5: String = The value of xyz is Xyz(Abc(a,b,c),x)

// type safe string interpolation
scala> ss"The value of xyz is $s"
res6: com.thaj.safe.string.interpolator.SafeString = SafeString(The value of xyz is { abc : { a : a, b : b, c : c }, name : x })

scala> res1.string
res7: String = The value of xyz is { abc : { a : a, b : b, c : c }, name : x }
```


This works for any level of **deep nested structure of case class**. This is done with the support of macro materializer in `Safe.scala`.
The main idea here is, if any field in any part of the nested case class isn't safe to be converted to string, it will throw a compile time.
Also, if any part of case classes has `Secret`s in it, the value will be hidden. More on this in `Secret / Password` section

### Typesafe pretty-print of Coproducts (sealed trait and subclasses)

```scala

  sealed trait Wave
    case class Bi() extends Wave
    case class Hello() extends Wave
    case class GoodBye(a: String, b: Int) extends Wave
    case object R extends Wave

    val a: Wave = Bi()

    val r = R

    val b: Wave = Hello()

    val c: Wave = GoodBye("john", 1)

    ss"$a, $b, $c, $r".string must_=== "Bi, Hello, { a : john, b : 1 }, R"

```