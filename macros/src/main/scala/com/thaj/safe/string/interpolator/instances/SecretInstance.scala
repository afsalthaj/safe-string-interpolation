package com.thaj.safe.string.interpolator.instances

import com.thaj.safe.string.interpolator.{ Safe, Secret }

trait SecretInstance {
  implicit def secretString[A]: Safe[Secret] =
    _ => "*****"
}

object secret extends SecretInstance
