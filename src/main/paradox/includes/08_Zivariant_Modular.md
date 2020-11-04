@@@ slide

@@@@ slide

### Zivariant modularity (1/4)

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

### Zivariant modularity (2/4)

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

### Zivariant modularity (3/4)

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

### Zivariant modularity (4/4)

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