@@@ slide { color=#222222 }

@@@@ slide

### Deriving

```scala
trait RightCovariant[<=>[_, +_]] {
  def deriveCovariant[A]:
      Covariant[({ type lambda[+B] = A <=> B })#lambda] =
    new Covariant[({ type lambda[+B] = A <=> B })#lambda] {
      def map[B, C](f: B => C): A <=> B => A <=> C =
        rightMap(f)
    }

  def rightMap[A, B, C](f: B => C): (A <=> B) => (A <=> C)
}
```

@@@@

@@@@ slide

### Deriving example

```scala
implicit def Function1Covariant[T]:
  Covariant[({ type lambda[+x] = T => x })#lambda] =
    Divariant.Function1Divariant.deriveCovariant
```

@@@@

@@@@ slide

### Deriving Hierarchy

![derive_hierarchy_3.svg](images/derive_hierarchy_3.svg)

@@@@

@@@