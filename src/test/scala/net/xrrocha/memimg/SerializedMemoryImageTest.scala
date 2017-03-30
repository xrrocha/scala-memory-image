package net.xrrocha.memimg

import net.xrrocha.memimg.storage.{FileIOStream, ObjectStreamStorageComponent}

object SerializedMemoryImageTest extends MemoryImageTest {

  lazy val filename = "target/bank.ser"

  def buildBank(): TestBank =
    new TestBank
      with ObjectStreamStorageComponent

}
