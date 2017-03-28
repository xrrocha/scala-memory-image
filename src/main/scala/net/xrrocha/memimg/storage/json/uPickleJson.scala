package net.xrrocha.memimg.storage.json

import scala.meta._

class uPickleJson extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val q"case class $name( ..$args ) extends ..$parents { ..$stats }" = defn

    q"""
      case class $name ( ..$args ) extends ..$parents with net.xrrocha.memimg.storage.json.ToJson {
        def toJson = upickle.default.write[ $name ](this)
        ..$stats
      }
      object ${Term.Name(name.value)} extends net.xrrocha.memimg.storage.json.FromJson[ $name ] {
        implicit val pkl = upickle.default.macroRW[ $name ]
        def fromJson(jsonString: String): $name = upickle.default.read[ $name ](jsonString)
      }
     """
  }
}