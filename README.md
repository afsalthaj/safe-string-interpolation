## Typesafe Interpolation


![alt text](https://travis-ci.org/afsalthaj/safe-string-interpolation.svg?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/safe-string-interpolation/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.afsalthaj/safe-string_2.12.svg)](http://search.maven.org/#search|gav|1|g%3A%22io.github.afsalthaj%22%20AND%20a%3A%22safe-string_2.12%22)


An insanely simple type driven approach to string interpolation, aiming at consistent, secure,  and only-human-readable logs and console outputs, and for safe string operations ! 

Checkout the project [website](https://afsalthaj.github.io/safe-string-interpolation/) for all information.

In near future, there will be integration with scalaz.Show / cats.Show. If you are in scalaz world, there is an alternative where you can use `z` interpolator that works with `Show` typeclass. You can get the same sort of functionality (that this library provides) by depending on scalaz-deriving-magnolia to get automatic show instances. If you need a zero dependency project that handles only one use-case, which is safe string interpolation, this library is for you.
