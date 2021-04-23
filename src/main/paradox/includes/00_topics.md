@@@ slide { color=#222222 }

@@@@ slide { image='images/scalac_logo.png' }
 
@@@@

@@@@ slide

### zio-prelude `F[_]`

Covariant - map the output
```scala
trait Covariant[F[+_]] {
  def map[A, B](f: A => B): F[A] => F[B]
}
```

Contravariant - prepare input
```scala
trait Contravariant[F[-_]] {
  def contramap[A, B](f: B => A): F[A] => F[B]
}
```

@@@@

@@@@ slide

### zio-prelude `F[_,_]`

map output and error
```scala
trait Bicovariant[<=>[+_, +_]] {
  def bimap[A,B,AA,BB](f: A => AA, g: B => BB):
      (A<=>B) => (AA<=>BB)
}
```
prepare input and map output
```scala
trait Divariant[:=>[-_, +_]] {
  def dimap[A,B,C,D](f: C => A, g: B => D):
      (A :=> B) => (C :=> D)
}
```
@@@@

@@@@ slide
### Modularity

Right Covariant as common part of Divariant and Bicovariant

```scala
trait RightCovariant[<=>[_, +_]] {
  def rightMap[A, B, C](f: B => C): (A <=> B) => (A <=> C)
}

trait Divariant[:=>[-_, +_]] extends RightCovariant[:=>] {
  def dimap[A,B,C,D](f: C=>A, g: B=>D): A:=>B => C:=>D
}

trait Bicovariant[<=>[+_, +_]] extends RightCovariant[<=>] {
  def bimap[A, B, AA, BB](f: A=>AA, g: B=>BB): A<=>B => AA<=>BB
}
```
@@@@

@@@@ slide

### Zivariant `Z[-_,+_,+_]`

Prepare input, map output and errors & more

```scala
trait Zivariant[Z[-_,+_,+_]] {
  def zimap[R,E,A,R1,E1,A1](
      r: R1 => R, e: E => E1, a: A => A1
  ): Z[R,E,A] => Z[R1,E1,A1]
}
```

@@@@

@@@