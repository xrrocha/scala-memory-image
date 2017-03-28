package net.xrrocha.memimg.storage.json

import net.xrrocha.memimg.storage.LineFormat

trait FromJson[A] {
  def fromJson(jsonString: String): A
}

trait ToJson {
  def toJson: String
}

trait JsonLineFormat extends LineFormat {
  def converters: Seq[FromJson[_]]

  protected[this] lazy val jsonConverters: String => FromJson[_] =
    converters.
      map { converter =>
        val name = converter.getClass.getSimpleName
        name.substring(0, name.length - 1) -> converter
      }.
      toMap

  def parseLine(line: String): Any = {
    val pos = line.indexOf('\t')
    val typeName = line.substring(0, pos)
    val jsonString = line.substring(pos + 1)
    jsonConverters(typeName).fromJson(jsonString)
  }

  def formatLine(obj: Any): String = {
    val typeName = getTypeNameFrom(obj)
    val jsonString = obj.asInstanceOf[ToJson].toJson.replaceAll("\r?\n", "") // ugh!
    s"$typeName\t$jsonString"
  }

  protected[this] def getTypeNameFrom(obj: Any): String =
    obj.getClass.getSimpleName
}
