@@@ slide

@@@@ slide

### Contravariant

Prepare input (things on negative position)
```scala
trait Contravariant[F[-_]] {

  def contramap[A, B](f: B => A): F[A] => F[B]
}
```

@@@@

@@@@ slide

### Contravariant laws
Prepare input that behaves nicely:

```scala
object Contravariant
  extends LawfulF.Contravariant[DeriveEq, Equal] {

  val identityLaw: Co1LawsF = new Co1LawsF("identityLaw") {
    def apply[F[-_]: DeriveEq, A: Equal](fa: F[A]) =
      fa.contramap(identity[A]) <-> fa
  }

  val compositionLaw: CoLawsF = new CoLawsF("compositionLaw") {
    def apply[F[-_]: DeriveEq, A: Equal, B: Equal, C: Equal](
          fa: F[A], f: B => A, g: C => B) =
      fa.contramap(f).contramap(g) <-> fa.contramap(f compose g)
  }
}
```

@@@@

@@@