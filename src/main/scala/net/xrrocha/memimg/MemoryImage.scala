package net.xrrocha.memimg

import net.xrrocha.memimg.storage.Storage

trait Transaction[S, A] {
  def executeOn(system: S): A
}

trait Query[S, A] {
  def queryOn(system: S): A
}

trait MemoryImage[S] {
  this: Storage =>

  def newSystem: S

  val transactions: Iterator[Transaction[S, _]] =
    Iterator.continually(read()).
      takeWhile(_.isDefined).
      map(_.get.asInstanceOf[Transaction[S, _]])

  val system: S = {

    val snapshot: S =
      read().
        map(_.asInstanceOf[S]).
        getOrElse(newSystem)

    transactions.foreach(_.executeOn(snapshot))

    snapshot
  }

  write(system)

  def executeTransaction[A](transaction: Transaction[S, A]): A = synchronized {
    write(transaction)
    transaction.executeOn(system)
  }

  def executeQuery[A](query: Query[S, A]): A = synchronized {
    query.queryOn(system)
  }
}
