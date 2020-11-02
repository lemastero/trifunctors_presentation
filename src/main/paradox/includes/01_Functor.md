@@@ slide

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

@@@