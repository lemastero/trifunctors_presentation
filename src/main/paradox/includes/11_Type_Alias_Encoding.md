@@@ slide { color=#222222 }

@@@@ slide

### Type alias encoding

```scala
type Covariant[F[+_]] =
  TriRightCovariant[({type lambda[R,E,+A] = F[A]})#lambda]
type Contravariant[F[-_]] =
  TriContravariant[({type lambda[-R,E2,A] = F[R]})#lambda]

type Divariant[:=>[-_, +_]] =
  TriDivariant[({type lambda[-R,E,+A] = R :=> A})#lambda]
type Bivariant[<=>[+_, +_]] =
  TriBivariant[({type lambda[R,+E,+A] = E <=> A})#lambda]
type RightCovariant[<=>[_, +_]] =
  TriRightCovariant[({type lambda[R,E,+A] = E <=> A})#lambda]
```

@@@@

@@@@ slide

### Support existing abstractions

```scala
trait CovariantInstance[F[+_]]
  extends Covariant[F]
  with CovariantSubset[F, AnyType]
  with Invariant[F] {
  
    def mapSubset[A, B: AnyType](f: A => B): F[A] => F[B] =
      map(f)
  
    def map[R, E, A, A1](f: A => A1): F[A] => F[A1]
}
```

@@@@

@@@@ slide

### Problems

@@@@@ fragments  
3 x Invariant for Covariant, Contravariant ?
Traversable
Lots of compile errors :)
@@@@@

@@@@

@@@