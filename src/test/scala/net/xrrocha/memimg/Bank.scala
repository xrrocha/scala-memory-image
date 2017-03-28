package net.xrrocha.memimg

import java.util.UUID

import net.xrrocha.memimg.storage.json.uPickleJson

import upickle.Js

import scala.collection.mutable.{Map => MMap}

object MutableMapRW {

  implicit val mmapReader: upickle.default.Reader[MMap[String, Account]] =
    new upickle.default.Reader[MMap[String, Account]] {
      def read0: PartialFunction[Js.Value, MMap[String, Account]] = {

        case obj: Js.Obj => MMap(obj.value.map { case (id, accountJs) =>

          val accountJsObj = accountJs.asInstanceOf[Js.Obj]
          val account: Account = implicitly[upickle.default.Reader[Account]].read(accountJsObj)

          id -> account

        }: _*)
      }
    }

  implicit val mmapWriter: upickle.default.Writer[MMap[String, Account]] =
    new upickle.default.Writer[MMap[String, Account]] {

      def write0: MMap[String, Account] => Js.Value = mmap => {

        val pairs: MMap[String, Js.Value] = mmap.map { case (id, account) =>
          id -> implicitly[upickle.default.Writer[Account]].write(account)
        }

        Js.Obj(pairs.toSeq: _*)
      }
    }
}
import MutableMapRW._


trait Entity {
  def id: String
  val creationTimestamp: Long = System.currentTimeMillis()
}
object Entity {
  type Id = String
  type Money = BigDecimal

  def nextId(): String = UUID.randomUUID().toString
}

import net.xrrocha.memimg.Entity._

@uPickleJson
case class Bank(id: String, accounts: MMap[String, Account] = MMap()) extends Entity {

  def allAccounts: Iterable[Account] = accounts.values

  def getAccount(id: String): Account = accounts(id)

  def addAccount(accountId: String, initialBalance: Money): Account = {
    val account = Account(accountId, initialBalance)
    accounts(account.id) = account
    account
  }

  def removeAccount(account: Account): Account = {
    accounts -= account.id
    account
  }

  def transfer(source: Account, target: Account, amount: Money): (Money, Money) = {
    require(amount > 0)
    source.addToBalance(-amount)
    target.addToBalance(amount)
    (source.balance, target.balance)
  }
}

case class Account(id: String, var balance: Money) extends Entity {
  require(balance >= 0)

  def addToBalance(amount: Money): Money = {
    val newBalance = balance + amount
    require(newBalance >= 0)
    balance = newBalance
    balance
  }
}

@uPickleJson
case class CreateAccount(id: String, initialBalance: Money) extends Transaction[Bank, Account] {

  require(initialBalance > 0, s"Invalid initial balance: $initialBalance")

  def executeOn(bank: Bank): Account = bank.addAccount(id, initialBalance)

}

@uPickleJson
case class RemoveAccount(accountId: String) extends Transaction[Bank, Account] {
  def executeOn(bank: Bank): Account = {

    val account = bank.getAccount(accountId)
    bank.removeAccount(account)

  }
}

@uPickleJson
case class Deposit(accountId: String, amount: Money) extends Transaction[Bank, Money] {
  def executeOn(bank: Bank): Money = {

    require(amount > 0, s"Invalid deposit amount: $amount")

    val account = bank.getAccount(accountId)
    account.addToBalance(amount)
  }
}

@uPickleJson
case class Withdraw(accountId: String, amount: Money) extends Transaction[Bank, Money] {
  def executeOn(bank: Bank): Money = {

    require(amount > 0, s"Invalid transfer amount: $amount")

    val account = bank.getAccount(accountId)
    require(account.balance >= amount, s"Insufficient funds for withdrawal ${account.id}")

    account.addToBalance(-amount)
  }
}

@uPickleJson
case class Transfer(sourceId: String, targetId: String, amount: Money) extends Transaction[Bank, (Money, Money)] {
  def executeOn(bank: Bank): (Money, Money) = {

    require(amount > 0, s"Invalid transfer amount: $amount")

    val source = bank.getAccount(sourceId)
    require(source.balance >= amount, s"Insufficient funds for transfer ${source.id}")

    val target = bank.getAccount(targetId)

    bank.transfer(source, target, amount)
  }
}

