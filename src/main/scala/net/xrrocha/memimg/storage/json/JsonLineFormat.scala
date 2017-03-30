package net.xrrocha.memimg.storage.json

import net.xrrocha.memimg.storage.LineFormatComponent


trait FromJson[A] {
  def fromJson(jsonString: String): A
}

trait ToJson {
  def toJson: String
}

trait JsonLineFormatComponent extends LineFormatComponent {

  def jsonConverters: Seq[FromJson[_]]

  lazy val lineFormat = new JsonLineFormat {}


  trait JsonLineFormat extends LineFormat {

    protected[this] lazy val jsonConverterMap: String => FromJson[_] =
      jsonConverters.
        map { converter =>
          val name = converter.getClass.getSimpleName
          name.substring(0, name.length - 1) -> converter
        }.
        toMap

    def parseLine(line: String): Any = {
      val pos = line.indexOf('\t')
      val typeName = line.substring(0, pos)
      val jsonString = line.substring(pos + 1)
      jsonConverterMap(typeName).fromJson(jsonString)
    }

    def formatLine(obj: Any): String = {
      val typeName = getTypeNameFrom(obj)
      val jsonString = obj.asInstanceOf[ToJson].toJson.replaceAll("\r?\n", "") // ugh!
      s"$typeName\t$jsonString"
    }

    protected[this] def getTypeNameFrom(obj: Any): String =
      obj.getClass.getSimpleName
  }

}
