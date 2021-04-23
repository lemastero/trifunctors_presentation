@@@ slide { color=#222222 }

@@@@ slide

## Zivariant -
### prepare input, map output and error

@@@@

@@@@ slide

### ZIO type aliases

```scala
type IO[+E, +A]   = ZIO[Any, E, A]
type Task[+A]     = ZIO[Any, Throwable, A]
type RIO[-R, +A]  = ZIO[R, Throwable, A]
type UIO[+A]      = ZIO[Any, Nothing, A]
type URIO[-R, +A] = ZIO[R, Nothing, A]
```
@@@@

@@@@ slide

### Classification of IO

```scala
IO[+A]         ~ () => A           // Monix Task (classic IO)
BIO[+E,+A]     ~ () => Either[E,A] // Monix BIO  (bifunctor IO)
PIO[-R,+A]     ~ R => A            // arrow      (profunctor IO)
Trio[-R,+E,+A] ~ R => Either[E,A]  // ZIO        (? IO)
```

@@@@

@@@@ slide

### Hidden abstraction
What type class is behind ZIO ?

```scala
IO[+A]         ~ () => A           // Monix Task (functor IO)
BIO[+E,+A]     ~ () => Either[E,A] // Monix BIO  (bifunctor IO)
PIO[-R,+A]     ~ R => A            // arrow      (profunctor IO)
Trio[-R,+E,+A] ~ R => Either[E,A]  // ZIO        (? IO)
```

@@@@

@@@