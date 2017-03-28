package net.xrrocha.io

import java.io.{InputStream, ObjectInputStream, ObjectStreamClass}

// This class avoids a spurious ClassNotFoundException thrown by ObjectInputStream
class ResolvingObjectInputStream(in: InputStream) extends ObjectInputStream(in) {
  override def resolveClass(desc: ObjectStreamClass): Class[_] =
      Class.forName(desc.getName, false, this.getClass.getClassLoader)
}