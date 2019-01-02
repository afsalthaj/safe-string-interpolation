## Typesafe Interpolation


![alt text](https://travis-ci.org/afsalthaj/safe-string-interpolation.svg?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/safe-string-interpolation/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.afsalthaj/safe-string_2.12.svg)](http://search.maven.org/#search|gav|1|g%3A%22io.github.afsalthaj%22%20AND%20a%3A%22safe-string_2.12%22)


A type driven approach to string interpolation, aiming at consistent, secure,  and only-human-readable logs and console outputs ! 

Checkout the project [website](https://afsalthaj.github.io/safe-string-interpolation/) for all information.


The alternate approach to this library is nothing but use `Show` interpolation in scalaz, with scalaz-deriving to automatically derive `Show` instances for products / case-class. The approach is more or less the same, except that `safe-string-interpolation` deliberately avoids automatic conversion of types to `String`, nor even doesn't allow you to do `toString` on anything !
