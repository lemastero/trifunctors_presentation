@@@ slide { color=#222222 }

@@@@ slide

### Common pattern

* ZIO
* R => Either[E,A]  

@@@@

@@@@ slide

### Covariant superpowers

ZIO

```scala
val loadEmployee: ZIO[Int, Throwable, Employee] = ???
val loadDetails: ZIO[Int, Throwable, EmployeeDetails] =
  loadEmployeeFromDb.map(getDetails)
```

R => Either[E,A]

```scala
def loadEmployee: Int => Either[Throwable, Employee] = ???
val loadDetails: Int => Either[Throwable, EmployeeDetails] =
  loadFromDb andThen { _.map(getDetails) }
```

@@@@

@@@@ slide

### Contravariant superpowers

ZIO

```scala
val loadEmployee: ZIO[Int, Throwable, Employee] = ???
val loadDetails: ZIO[EmployeeId, Throwable, Employee] =
  loadEmployeeFromDb.contramap(idFromEmployee)
```

R => Either[E,A]

```scala
val loadEmployee: Int => Either[Throwable, Employee] = ???
val loadDetails: EmployeeId => Either[Throwable, Employee] =
  loadFromDb compose idFromEmployee
```

@@@@

@@@@ slide

### Bicovariant superpowers

ZIO

```scala
val loadEmployee: ZIO[Int, Throwable, Employee] = ???
val loadDetails: ZIO[Int, AppError, EmployeeDetails] =
  loadEmployeeFromDb.bimap(asDomainError, getDetails)
```

R => Either[E,A]

```scala
val loadEmployee: Int => Either[Throwable, Employee] = ???
val loadDetails: Int => Either[AppError, EmployeeDetails] =
  loadFromDb andThen { _.bimap(asDomainError, getDetails) }
```

@@@@


@@@@ slide

### Divariant superpowers

ZIO

```scala
val loadEmployeeFromDb: ZIO[Int, Throwable, Employee] = ???
val loadEmployee: ZIO[EmployeeId, Throwable, EmployeeDetails] =
  loadEmployeeFromDb.dimap(idFromEmployee, getDetails)
```

R => Either[E,A]

```scala
val loadEmployee: Int => Either[Throwable, Employee] = ???
val loadDet: EmployeeId => Either[Throwable, EmployeeDetails] =
  loadFromDb
    .andThen{ _.map(getDetails) }
    .compose idFromEmployee
```

@@@@

@@@@ slide

### Divariant & Bicovariant enough?

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
* 2 x Divariant
* Bicovariant
* 2 x Covariant
* Contravariant

@@@@

@@@@ slide

### 1 abstraction to rule them all (1/2)

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

### 1 abstraction to rule them all (2/2)

We can express others by:
```scala
trait Zivariant[Z[-_,+_,+_]] {
  def contramap[R,E,A,R1](r: R1 => R): Z[R,E,A] => Z[R1,E,A]
  def mapLeft[R,E,A,E1](e: E => E1): Z[R,E,A] => Z[R,E1,A]
  def map[R,E,A,A1](a: A => A1): Z[R,E,A] => Z[R,E,A1]
}
```

@@@@

@@@@ slide

### map, map error and contramap all in one

```scala
trait Zivariant[Z[-_,+_,+_]] {

  def zimap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E => E1, a: A => A1
  ): Z[R,E,A] => Z[R1,E1,A1]
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

Instance:
```scala
new Zivariant[({type lambda[-R,+E,+A]=R=>Either[E,A]})#lambda] {
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

### Use Zivariant R => Either[E,A]

Usage:
```scala
import Zivariant.FunctionEitherZivariant.zimap

val loadEmployee: Int => Either[Throwable, Employee] = ???
val getEmployeeDetails: EmployeeId => Either[AppError, EmployeeDetails] =
  zimap(idFromEmployee, asDomainError, getDetails)(loadFromDb)

val result: Either[AppError, EmployeeDetails] = getEmployeeDetails(EmployeeId(4))
result == Right(EmployeeDetails("Londo Mollari"))
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

@@@@

@@@@ slide

### Zivariant special Functor

![zimap5.svg](images/zimap5.svg)

@@@@

@@@