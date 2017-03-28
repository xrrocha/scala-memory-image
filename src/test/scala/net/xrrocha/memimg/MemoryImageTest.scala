package net.xrrocha.memimg

import java.io.File

import net.xrrocha.memimg.storage.{FileStreamIO, Storage}
import utest._
import utest.framework.{Test, Tree}

import scala.concurrent.ExecutionContext

trait MemoryImageTest extends TestSuite {

  import Entity._

  protected[this] def filename: String

  protected[this] def buildBank(): TestBank

  private[this] lazy val storageFile: File = {

    val file = new File(filename)
    require(!file.exists() || file.delete(), s"Can't initialize file: $file")
    file
  }

  trait TestBank extends MemoryImage[Bank] with FileStreamIO {
    this: Storage =>

    def newSystem: Bank = Bank(nextId())

    lazy val file: File = storageFile

  }

  private[this] var bank: TestBank = _

  override def utestWrap(runBody: => concurrent.Future[Any])
                        (implicit ec: ExecutionContext): concurrent.Future[Any] = {

    bank = buildBank()

    runBody.onComplete { _ =>
      bank.close()
    }

    concurrent.Future(())
  }

  val tests: Tree[Test] = this {

    'nonExistentStorageFileCreatesNewBank {

      assert(bank.executeQuery[Iterable[Account]](_.allAccounts).isEmpty)

      val account1 = bank.executeTransaction(CreateAccount(nextId(), 1234.56))
      assert(bank.executeQuery[Iterable[Account]](_.allAccounts).size == 1)
      assert(account1.balance == 1234.56)

      bank.executeTransaction(Deposit(account1.id, 1000))
      assert(account1.balance == 2234.56)

      bank.executeTransaction(Withdraw(account1.id, .56))
      assert(account1.balance == 2234)

      val account2 = bank.executeTransaction(CreateAccount(nextId(), 6543.21))
      assert(account2.balance == 6543.21)
      assert(bank.executeQuery[Iterable[Account]](_.allAccounts).size == 2)

      bank.executeTransaction(Withdraw(account2.id, .21))
      assert(account2.balance == 6543)

      val (balance1, balance2) = bank.executeTransaction(Transfer(account1.id, account2.id, 1000))
      assert(balance1 == 1234)
      assert(balance2 == 7543)

      val accounts = bank.executeQuery[Set[Account]](_.allAccounts.toSet)
      assert(accounts == Set(account1, account2))

    }

    'loadsFromExistingStorageFile {

      val balances = bank.executeQuery[Set[Money]](_.allAccounts.map(_.balance).toSet)
      assert(balances == Set[Money](1234, 7543))

    }

    'supportsQueries {

      val wellFundedAccounts =
        bank.executeQuery[Iterable[Account]](_.allAccounts.filter(_.balance >= 1500))

      assert(wellFundedAccounts.size == 1)
      assert(wellFundedAccounts.head.balance == 7543)

    }

  }
}
