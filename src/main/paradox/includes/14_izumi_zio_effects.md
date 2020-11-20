@@@ slide { color=#222222 }

@@@@ slide

### Zifunctor effects

@@@@

@@@@ slide
    
### Zivariant as base Effect ?

```scala
def getEmployeeDetails[F[_,_,_]](
  get: F[Int,Throwable,Employee])(implicit
  ZF: Zivariant[F]): F[EmployeeId, AppError, EmployeeDetails] = {
    def idFromEmployee: EmployeeId => ???
    def asDomainError: Throwable => AppError = ???
    def getDetails: Employee => EmployeeDetails = ???

    ZF.zimap(idFromEmployee, asDomainError, getDetails)(get)
}

val loadEmployeeFromDb: ZIO[Int, Throwable, Employee] = ???

val loadEmployee: ZIO[EmployeeId, AppError, EmployeeDetails] =
  getEmployeeDetails(loadEmployeeFromDb)
}
```

@@@@

@@@@ slide

### How about combine two Zifunctors?

```scala
val parseAvro: F[String, AvroErr, AvroModel] = ???
val processMsg: F[AvroModel, AppError, Result] = ???

parseAvro magic processMsg
```

@@@@

@@@@ slide

### Need more power

@@@@@ fragments  
add support for Monad for A ?  
lift Associative for error ?  
add support for Divisible for R ?  
restrict type of A or all types: R,E,A ?  
@@@@@

@@@@

@@@@ slide

### WIP Zivariant + Monad

```scala
trait TriAssocFlat[T[-_,+_,+_]] extends Zivariant[T] {
  def flatten[R,E,A,B](fa: T[T[R,E,A],E,A]): T[R,E,B]
  def flatMap[R,E,A,B](fa: T[R,E,A])(f: A => T[R,E,B]): T[R,E,B]
}

trait TriIdentityFlat[T[-_,+_,+_]] extends TriAssocFlat[T] {
  def pure[R,E,A](a: R => Either[E,A]): T[R,E,A]
  def pure[R,A](a: R => A): T[R,Nothing,A]
  def pure[A](a: A): T[Any,Nothing,A]

  def any[R,E,A]: T[Any,Nothing,Any]
}
```

@@@@

@@@@ slide

### Libraries

* @link[izumi bio](https://izumi.7mind.io/bio)  
* @link[TinkoffCreditSystems/tofu](https://github.com/TinkoffCreditSystems/tofu)
* @link[lemastero/Triglav](https://github.com/lemastero/Triglav)    

@@@@

@@@@ slide

### WIP Zifunctor - Get

```scala
type Optics[P[-_, +_], -S, +T, +A, -B] = P[A,B] => P[S,T]

def trifunctorGet[P[-_, +_], B](implicit PP: Profunctor[P]) = {
  type Get[-S, +T, +A] = Optics[P, S, T, A, B]

  new Zifunctor[Get] {
    override def zimap[E, A, R, EE, AA, RR](
        fa: P[R, B] => P[E, A]
    )(f: EE => E, g: A => AA, h: R => RR): P[RR,B] => P[EE,AA] =
      rrb => {
        val r1: P[R, B] = PP.contramap(rrb)(h)
        val r2: P[E, A] = fa(r1)
        PP.dimap(r2)(f, g)
}}}
```

@@@@

@@@@ slide

### WIP Fnfunctor - Set

```scala
def trifunctorSet[P[-_, +_], A](implicit PP: Profunctor[P]) = {
  type Set[-S, -B, +T] = Optics[P, S, T, A, B]

  new Fnfunctor[Set] {
    override def fnmap[E, A1, R, EE, AA, RR](
        fa: P[A, A1] => P[E, R]
    )(f: EE => E, g: AA => A1, h: R => RR): P[A,AA] => P[EE,RR] =
      a => {
        val r1: P[A, A1] = PP.map(a)(g)
        val r2: P[E, R] = fa(r1)
        PP.dimap(r2)(f, h)
      }
  }
}
```

@@@@

@@@