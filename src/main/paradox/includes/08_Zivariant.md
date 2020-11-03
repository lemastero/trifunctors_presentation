@@@ slide

@@@@ slide

### Problem 1
What type class is behind ZIO ?

```scala
IO[+A]         ~ () => A           // Monix Task (functor IO)
BIO[+E,+A]     ~ () => Either[E,A] // Monix BIO  (bifunctor IO)
PIO[-R,+A]     ~ R => A            // arrow      (profunctor IO)
Trio[-R,+E,+A] ~ R => Either[E,A]  // ZIO        (? IO)
```

@@@@

@@@@ slide

### Instances for ZIO (1/3)
There are 2 ways to express Divariant for ZIO

```scala
def ZIODiv[E] = new Divariant[ZIO[*,E,*]] {
  def leftContra[R,A,RR](f: RR=>R): ZIO[R,E,A] => ZIO[RR,E,A] =
    zio => zio.provideSome(f)
  def rightMap[R,A,A1](g: A=>A1): ZIO[R,E,A]=>ZIO[R,E,A1] =
    _.map(g)
}
def FailedZIODiv[A] = new Divariant[ZIO[*,*,A]] {
  def leftContra[R,E,RR](f: RR=>R): ZIO[R,E,A] => ZIO[RR,E,A] =
    zio => zio.provideSome(f)
  def rightMap[R,E,E1](f: E=>E1): ZIO[R,E,A]=>ZIO[R,E1,A] =
    _.mapError(f)
}
```

@@@@

@@@@ slide

### Instances for ZIO (2/3)
2 identical implementations for leftContramap

```scala
def ZIODiv[E] = new Divariant[ZIO[*,E,*]] {
  def leftContra[R,A,RR](f: RR=>R): ZIO[R,E,A] => ZIO[RR,E,A] =
    zio => zio.provideSome(f)
  // ...
}
def FailedZIODiv[A] = new Divariant[ZIO[*,*,A]] {
  def leftContra[R,E,RR](f: RR=>R): ZIO[R,E,A] => ZIO[RR,E,A] =
    zio => zio.provideSome(f)
  // ...
}
```

@@@@

@@@@ slide

### Instances for ZIO (3/3)
There are 2 rightMap that does different thing

```scala
def ZIODiv[E] = new Divariant[ZIO[*,E,*]] {
  // ...
  def rightMap[R,A,A1](g: A=>A1): ZIO[R,E,A]=>ZIO[R,E,A1] =
    _.map(g)
}
def FailedZIODiv[A] = new Divariant[ZIO[*,*,A]] {
  // ...
  def rightMap[R,E,E1](f: E=>E1): ZIO[R,E,A]=>ZIO[R,E1,A] =
    _.mapError(f)
}
```

@@@@

@@@@ slide

### Too many instances:
```scala
ZIO[-R,+E,+A]
```
* 2 Divariant
* Bicovariant
* 2 x Covariant
* Contravariant

@@@@

@@@@ slide

### One abstraction to rule them all

```scala
trait Zivariant[Z[-_,+_,+_]] {

  def contramap[R,E,A,R1](r: R1 => R): Z[R,E,A] => Z[R1,E,A]

  def mapLeft[R,E,A,E1](e: E => E1): Z[R,E,A] => Z[R,E1,A]

  def map[R,E,A,A1](a: A => A1): Z[R,E,A] => Z[R,E,A1]

  def bimap[R,E,A,E1,A1](e: E=>E1,a: A=>A1): Z[R,E,A]=>Z[R,E1,A1]

  def dimap[R,E,A,RR,AA](r: RR=>R,a: A=>AA): Z[R,E,A]=>Z[RR,E,AA]
}
```

@@@@

@@@@ slide

### map, map Error and contramap all in one

```scala
trait Zivariant[Z[-_,+_,+_]] {

  def zimap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): Z[R,E,A] => Z[R1,E1,A1]
  
  // ...
}
```

@@@@

@@@@ slide

### Just zimap this thing :)

```scala
trait Zivariant[Z[-_,+_,+_]] {

  def contramap[R,E,A,R1](r: R1 => R) = zimap(r, id[E], id[A])

  def mapLeft[R,E,A,E1](e: E => E1)   = zimap(id[R], e, id[A])

  def map[R,E,A,A1](a: A => A1)       = zimap(id[R], id[E], a)

  def bimap[R,E,A,E1,A1](e: E=>E1,a: A=>A1) = zimap(id[R],e,a)

  def dimap[R,E,A,RR,AA](r: RR=>R,a: A=>AA) = zimap(r,id[E],a)
}
```

@@@@

@@@@ slide

### Zivariant Instance - ZIO

```scala
new Zivariant[ZIO] {
  def zimap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): ZIO[R,E,A] => ZIO[R1,E1,A1] =
    rea => rea.bimap(e,a).provideSome(r)
}
```

@@@@

@@@@ slide

### Zivariant R => Either[E,A]

```scala
new Zivariant[({type lambda[-R,+E,+A] = R=>Either[E,A]})#lambda] {
  override def zimap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): (R => Either[E,A]) => R1 => Either[E1,A1] =
    rea => r1 =>
      (r andThen rea)(r1) match {
        case Right(aa) => Right(a(aa))
        case Left(ee)  => Left(e(ee))
      }
}
```

@@@@

@@@@ slide

### Zivariant R => (E,A)

```scala
new Zivariant[({type lambda[-R,+E,+A] = R=>(E,A)})#lambda] {
  def zimap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): (R=>(E,A)) => R1=>(E1,A1) =
    rea => r1 => {
      val (ee, aa) = (r andThen rea)(r1)
      (e(ee), a(aa))
    }
}
```

@@@@

@@@@ slide

## Othere Zivariant instances

```scala
ZIO[-R, +E, +A]
ZLayer[-RIn, +E, +ROut]
ZManaged[-R, +E, +A]
ZStream[-R, +E, +O]
ZSTM[-R, +E, +A]
```

@@@@

@@@@ slide

## Zivariant modularity (1/4)

```scala
trait TriRightCovariant[Z[_,_,+_]] {
  def map[R,E,A,A1](f: A=>A1): Z[R,E,A]=>Z[R,E,A1]
}
trait TriLeftCovariant[Z[_,+_,_]] {
  def mapLeft[R,E,A,E1](e: E=>E1): Z[R,E,A]=>Z[R,E1,A]
}
trait TriBivariant[Z[_,+_,+_]]
    extends TriRightCovariant[Z] with TriLeftCovariant[Z] {
  def bimap[R,E0,A,E1,A1](e: E0=>E1, a: A=>A1): Z[R,E0,A]=>Z[R,E1,A1]
}
```

@@@@

@@@@ slide

## Zivariant modularity (2/4)

```scala
trait TriRightCovariant[Z[_,_,+_]] {
  def map[R,E,A,A1](f: A=>A1): Z[R,E,A]=>Z[R,E,A1]
}
trait TriContravariant[Z[-_,_,_]] {
  def contramap[R,E2,A,R1](r: R1=>R): Z[R,E2,A] => Z[R1,E2,A]
}
trait TriDivariant[Z[-_,_,+_]]
    extends TriContravariant[Z] with TriRightCovariant[Z] {
  def dimap[R,E,A,R1,A1](r: R1=>R, a: A=>A1): Z[R,E,A]=>Z[R1,E,A1]
}
```

@@@@

@@@@ slide

## Zivariant modularity (3/4)

```scala
trait TriLeftCovariant[Z[_,+_,_]] {
  def mapLeft[R,E,A,E1](e: E=>E1): Z[R,E,A]=>Z[R,E1,A]
}
trait TriContravariant[Z[-_,_,_]] {
  def contramap[R,E2,A,R1](r: R1=>R): Z[R,E2,A] => Z[R1,E2,A]
}
trait TriLeftDivariant[Z[-_,+_,_]] extends TriContravariant[Z] with TriLeftCovariant[Z] {
  def dimapLeft[R,E,A,R1,E1](r: R1=>R, e: E=>E1): Z[R,E,A]=>Z[R1,E1,A]
}
```

@@@@

@@@@ slide

## Zivariant modularity (4/4)

```scala
trait Zivariant[Z[-_, +_, +_]]
    extends TriBivariant[Z]
    with TriLeftDivariant[Z]
    with TriDivariant[Z] {
  def zimap[R,E,A,R1,E1,A1](r: R1 => R, e: E => E1, a: A => A1): Z[R,E,A] => Z[R1,E1,A1]
}
```

@@@@

@@@