@@@ slide { color=#222222 }

@@@@ slide

### Deriving

```scala
trait TriRightCovariant[Z[_, _, +_]]
    extends Trinvarint[Z] { self =>

  def deriveCovariant[R0, E0] =
    new Covariant[({ type lambda[+A] = Z[R0, E0, A] })#lambda] {
      def map[R,E,A,A1](f: A => A1): Z[R0,E0,A] => Z[R0,E0,A1] =
        self.map(f)
    }

  def map[R, E, A, A1](f: A => A1): Z[R, E, A] => Z[R, E, A1]
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

### Deriving Failure

```scala
trait TriLeftCovariant[Z[_,+_,_]] extends Trinvarint[Z] { self =>
  def deriveFailureCovariant[R0, A0] =
    new Covariant[({type lambda[+E] = Failure[Z[R0,E,A0]]})#lambda] {

      def map[R,E,A,A1](a: A => A1) = fz => {
        val fz2: Z[R0,A,A0] => Z[R0,A1,A0] = self.mapLeft(a)
        Failure.wrap(fz2(Failure.unwrap(fz)))
      }
    }

  def mapLeft[R,E,A,E1](e: E => E1): Z[R,E,A] => Z[R,E1,A]
}
```

@@@@

@@@@ slide

### Deriving Hierarchy

![derive_hierarchy_3.svg](images/derive_hierarchy_3.svg)

@@@@

@@@