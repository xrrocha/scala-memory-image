package net.xrrocha.memimg.storage

trait StorageComponent {

  val storage: Storage

  trait Storage {
    def read(): Option[Any]

    def write(any: Any): Unit
  }

}
