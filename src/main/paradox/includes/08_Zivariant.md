@@@ slide { color=#242220 }

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
    
### Zivariant R => bicovariant[E,A]

```scala
def fromFunctionBicovariant[<=>[+_, +_]](implicit
  ev: Bicovariant[<=>]
): Zivariant[({ type lambda[-R, +E, +A] = R => E <=> A })#lambda] =
  new Zivariant[({ type lambda[-R, +E, +A] = R => E <=> A })#lambda] {
    override def zimap[R, E, A, R1, E1, A1](
      r: R1 => R,
      e: E => E1,
      a: A => A1
    ): (R => <=>[E, A]) => R1 => <=>[E1, A1] =
      rea => r1 => (r andThen rea)(r1).bimap(e, a)
  }
```

@@@@

@@@@ slide

### ZIO Zivariant

```scala
implicit val ZioZivariant: Zivariant[ZIO] = new Zivariant[ZIO] {
  override def zimap[R, E, A, R1, E1, A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): ZIO[R,E,A] => ZIO[R1,E1,A1] =
    rea => rea.bimap(e, a).provideSome(r)
}
```

@@@@

@@@@ slide

### ZStream Zivariant

```scala
implicit val ZStreamZivariant: Zivariant[ZStream] =
  new Zivariant[ZStream] {
    override def zimap[E, A, R, EE, AA, RR](
      r: EE => E,
      e: A => AA,
      a: R => RR
    ): ZStream[E, A, R] => ZStream[EE, AA, RR] =
      rea => rea.bimap(e, a).provideSome(r)
  }
```

@@@@

@@@@ slide

### ZSTM Zivariant

```scala
implicit val ZSTMZivariant: Zivariant[ZSTM] =
  new Zivariant[ZSTM] {
    override def zimap[E, A, R, EE, AA, RR](r: EE => E, e: A => AA, a: R => RR): ZSTM[E, A, R] => ZSTM[EE, AA, RR] =
      rea => rea.bimap(e, a).provideSome(r)
  }
```

@@@@

@@@@ slide

### ZLayer Zivariant

```scala
implicit val ZLayerZivariant: Zivariant[ZLayer] =
  new Zivariant[ZLayer] {
    override def zimap[E, A, R, EE, AA, RR](
      r: EE => E,
      e: A => AA,
      a: R => RR
    ): ZLayer[E, A, R] => ZLayer[EE, AA, RR] =
      rea => ZLayer.fromFunctionMany(r) >>> rea.map(a).mapError(e)
  }
```

No bimap, no provideSome for ZLayer ?

@@@@

@@@@ slide

### ZManaged Zivariant

```scala
implicit val ZManagedZivariant: Zivariant[ZManaged] =
  new Zivariant[ZManaged] {
    override def zimap[E, A, R, EE, AA, RR](
      r: EE => E,
      e: A => AA,
      a: R => RR
    ): ZManaged[E, A, R] => ZManaged[EE, AA, RR] =
      rea => rea.bimap(e, a).provideSome(r)
  }
```

No bimap, no provideSome for ZLayer ?

@@@@

@@@@ slide

### Othere Zivariant instances

```scala
ZIO[-R, +E, +A]
ZLayer[-RIn, +E, +ROut]
ZManaged[-R, +E, +A]
ZStream[-R, +E, +O]
ZSTM[-R, +E, +A]
```

@@@@

@@@@ slide
    
### Zivariant example

```scala
import Zivariant.FunctionEitherZivariant.zimap

val parseAvro: String => Either[Throwable, AvroModel] = ???

def getContent: KafkaMessage => String = ???
def handleError: Throwable => AppError = ???
def toDomainModel: AvroModel => DomainModel = ???

val kafkaHandler = zimap(getContent, handleError, toDomainModel)
val price: KafkaMessage => Either[AppError, DomainModel] =
  kafkaHandler(parseAvro)
```

@@@@

@@@