---
layout: docs
title:  "Logging Secrets"
section: "main_menu"
position: 4
---

## Logging secrets

As mentioned before, just wrap the secret with Secret.apply. 


```scala

scala> import com.thaj.safe.string.interpolator.SafeString._
import com.thaj.safe.string.interpolator.SafeString._

scala> import com.thaj.safe.string.interpolator.Secret
import com.thaj.safe.string.interpolator.Secret

scala> case class DbConn(driver: String, password: Secret)
defined class DbConn

scala> val dbConn = DbConn("driverstring", Secret("adifficultpassword"))
dbConn: DbConn = DbConn(driverstring,Secret(adifficultpassword))

scala> s"The db connection is $dbConn"
res2: String = The db connection is DbConn(driverstring,Secret(adifficultpassword))

scala> safeStr"The db connection is $dbConn"
res3: com.thaj.safe.string.interpolator.SafeString = SafeString(The db connection is { password: ******************, driver: driverstring })


```

**Secrets will be hidden wherever it exists in your nested case class**

## Your own secret ?

If you don't want to use `interpolation.Secret` data type and need to use your own, then define `Safe` instance for it.

```scala
case class MySecret(value: String) extends AnyVal

implicit val safeMySec: Safe[MySecret] = _ => "****"

val conn = DbConnection("posgr", MySecret("this will be hidden"))


scala> safeStr"the db is $conn"
res1: com.thaj.safe.string.interpolator.SafeString = SafeString(the db is { password: ****, name: posgr })

```