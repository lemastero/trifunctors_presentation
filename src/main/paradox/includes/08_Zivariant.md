@@@ slide

@@@@ slide

## Problem 1
What type class is behind ZIO ?

```scala
IO[+A]         ~ () => A           // Monix Task (functor IO)
BIO[+E,+A]     ~ () => Either[E,A] // Monix BIO  (bifunctor IO)
PIO[-R,+A]     ~ R => A            // arrow      (profunctor IO)
Trio[-R,+E,+A] ~ R => Either[E,A]  // ZIO        (? IO)
```

@@@@

@@@@ slide

## Problem 2
There are 2 ways to express Divariant for ZIO, ....

```scala
TODO
```

@@@@

@@@