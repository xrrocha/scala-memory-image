package net.xrrocha.memimg.storage

import java.io._

import scala.util.Try

trait IOStream {
  def in: InputStream
  def out: OutputStream
}

trait FileIOStream extends IOStream {

  def file: File

  private[this] lazy val checkedFile = {
    if (file.exists()) {
      val perms = Seq[File => Boolean](_.isFile, _.canRead, _.canWrite)
      require(perms.forall(_(file)), "Invalid or inaccessible file: $file")
    } else {
      file.getParentFile.mkdirs()
      require(file.createNewFile(), "Can't create new file: $file")
    }
    file
  }

  lazy val in = new FileInputStream(checkedFile)
  lazy val out = new FileOutputStream(checkedFile)
}
