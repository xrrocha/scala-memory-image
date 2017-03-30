package net.xrrocha.memimg.storage

import java.io.{EOFException, ObjectOutputStream}

import scala.util.{Failure, Success, Try}

trait ObjectStreamStorageComponent extends StorageComponent {

  this: IOStream =>

  lazy val storage = new ObjectStreamStorage {}

  trait ObjectStreamStorage extends Storage {

    import net.xrrocha.io.ResolvingObjectInputStream

    lazy private[this] val inputObjectStream = new ResolvingObjectInputStream(in)
    lazy private[this] val outputObjectStream = new ObjectOutputStream(out)

    def read(): Option[Any] = Try(inputObjectStream.readObject()) match {
      case Success(obj) =>
        Some(obj)
      case Failure(_: EOFException) =>
        // Try(inputObjectStream.close())
        None
      case Failure(error) =>
        throw error
    }

    def write(any: Any): Unit = {
      outputObjectStream.writeObject(any)
      outputObjectStream.flush()
    }
  }

}
