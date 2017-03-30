package net.xrrocha.memimg.storage

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}

import scala.util.{Failure, Success, Try}

trait LineFormatComponent {

  val lineFormat: LineFormat

  trait LineFormat {
    def parseLine(line: String): Any
    def formatLine(any: Any): String
  }
}

trait LineStreamStorageComponent extends StorageComponent {

  this: IOStream with LineFormatComponent =>

  lazy val storage = new LineStreamStorage {}

  trait LineStreamStorage extends Storage {

    lazy private[this] val reader = new BufferedReader(new InputStreamReader(in))
    lazy private[this] val writer = new PrintWriter(new OutputStreamWriter(out), true)

    def read(): Option[Any] = Try(reader.readLine()) match {
      case Success(line) if line != null =>
        Some(lineFormat.parseLine(line))
      case Success(line) if line == null =>
        None
      case Failure(error) =>
        throw error
    }

    def write(any: Any): Unit =
      writer.println(lineFormat.formatLine(any))
  }

}
