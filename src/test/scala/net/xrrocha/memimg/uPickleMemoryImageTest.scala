package net.xrrocha.memimg

import net.xrrocha.memimg.storage.LineStreamStorageComponent
import net.xrrocha.memimg.storage.json.JsonLineFormatComponent

object uPickleMemoryImageTest extends MemoryImageTest {

  lazy val filename = "target/bank.mjson"

  def buildBank(): TestBank =
    new TestBank
      with LineStreamStorageComponent with JsonLineFormatComponent {

      lazy val jsonConverters =
        Seq(Bank, CreateAccount, RemoveAccount, Deposit, Withdraw, Transfer)
    }
}
