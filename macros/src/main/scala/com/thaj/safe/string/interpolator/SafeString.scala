package com.thaj.safe.string.interpolator


final case class Field[T : Safe](name: String, value: T) {
  override def toString: String = s"""$name: ${Safe[T].value(value)}"""
}

final case class SafeString(string: String) {
  def +(that: SafeString): SafeString = SafeString(this.string ++ that.string)
}
