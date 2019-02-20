---
layout: docs
title:  "Simple Example"
section: "main_menu"
position: 1
---


## Simple Example
```scala

scala> import com.thaj.safe.string.interpolator.SafeString._
import SafeString._

scala> val a: String = "ghi"
a: String = ghi

scala> val b: String = "xyz"
b: String = xyz

scala> class C
defined class C

scala> val c = new C
res1: C = C@54e3ae35

// unsafe interpolation
scala> s"The a, b and c are: ${a}, ${b}, ${c}"
res2: String = The a, b and c are: ghi, xyz, C@3aaeb14


// safeStr interpolation
scala> safeStr"The a, b and c are: ${a}, ${b}, ${c}"
<console>:18: error: unable to find a safe instance for class C. Make sure it is a case class or a type that has safe instance.
                                                                                                    ^
scala> safeStr"a and b: ${a}, ${b}"
res2: com.thaj.safe.string.interpolator.SafeString = SafeString(a and b: ghi, xyz)

```