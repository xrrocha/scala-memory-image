package net.xrrocha.memimg.storage

trait Storage {
  def read(): Option[Any]

  def write(any: Any): Unit

  def close(): Unit
}
