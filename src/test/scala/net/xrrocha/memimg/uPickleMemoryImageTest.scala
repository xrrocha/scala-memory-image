package net.xrrocha.memimg

import net.xrrocha.memimg.storage.json.JsonLineFormat
import net.xrrocha.memimg.storage.{FileStreamIO, LineStreamStorage}

object uPickleMemoryImageTest extends MemoryImageTest {

  lazy val filename = "target/bank.mjson"

  def buildBank(): TestBank =
    new TestBank
      with LineStreamStorage with JsonLineFormat {

      lazy val converters =
        Seq(Bank, CreateAccount, RemoveAccount, Deposit, Withdraw, Transfer)
    }
}
