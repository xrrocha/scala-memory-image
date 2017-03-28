# Scala MemoryImage POC #

This repo contains a skeletal Scala implementation of the
[Memory Image](https://www.martinfowler.com/bliki/MemoryImage.html)
pattern where transactional data is kept in main memory and backed
by a serialized snapshot of the system's data along with a journal
of all transactions applied to it. In Martin Fowler words:

> The key element to a memory image is using event sourcing,
> which essentially means that every change to the application's
> state is captured in an event which is logged into a persistent store.
> Furthermore it means that you can rebuild the full application state
> by replaying these events. The events are then the primary
> persistence mechanism.

Two storage mechanisms are used in this POC: plain old Java
serialization and
[uPickle](http://www.lihaoyi.com/upickle-pprint/upickle/) JSON. An
annotation macro is used to generate concrete type references as
expected by uPickle but also selectable at runtime without incurring in
reflection.
