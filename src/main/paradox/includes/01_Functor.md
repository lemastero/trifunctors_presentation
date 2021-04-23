@@@ slide { color=#222222 }

@@@@ slide

## Covariant -
### map things

@@@@

@@@@ slide

### map is very common

```scala
List(2,3,4).map(isOdd) == List(false, true, false)
Option(42).map(isOdd) == Option(false)
Right("Success").map(_.length) == Right(7)
```
@@@@

@@@@ slide

### map ZIO

```scala
val loadEmployee: ZIO[EmployeeId, DbError, Employee] = ???

def getDetails(e: Employee): EmployeeDetails = ???

val loadDetails: ZIO[EmployeeId, DbError, EmployeeDetails] =
  loadEmployee.map(getDetails)
```
@@@@

@@@@ slide

### Covariant
map things we have or can compute (they are on positive position)

```scala
trait Covariant[F[+_]] {
  def map[A, B](f: A => B): F[A] => F[B]
}
```
@@@@

@@@@ slide

### Covariant laws
Mapping that behave nicely:

```scala
object Covariant extends LawfulF.Covariant[DeriveEq, Equal] {

  val identityLaw: C1LawsF = new C1LawsF("identityLaw") {
    def apply[F[+_]: DeriveEq, A: Equal](fa: F[A]) =
      fa.map(identity) <-> fa
  }

  val compositionLaw: CLawsF = new CLawsF("compositionLaw") {
    def apply[F[+_]: DeriveEq, A: Equal, B: Equal, C: Equal](
          fa: F[A], f: A => B, g: B => C) =
      fa.map(f).map(g) <-> fa.map(f andThen g)
  }
}
```

@@@@

@@@@ slide

### Covariant special Functor

![map2.svg](images/map2.svg)

@@@@

@@@