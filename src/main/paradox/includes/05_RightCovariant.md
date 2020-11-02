@@@ slide

@@@@ slide
### Divariant ∩ Bivariant ?

```scala
trait Divariant[:=>[-_, +_]] {
  def rightMap[A,B,C](f: B=>C): A:=>B => A:=>C

  def leftContramap[A,B,C](f: C=>A): A:=>B => C:=>B
  def dimap[A,B,C,D](f: C=>A, g: B=>D): A:=>B => C:=>D
}

trait Bicovariant[<=>[+_, +_]] {
  def rightMap[A,B,C](f: B=>C): A<=>B => A<=>C

  def leftMap[A, B, AA](f: A=>AA): A<=>B => AA<=>B
  def bimap[A, B, AA, BB](f: A=>AA, g: B=>BB): A<=>B => AA<=>BB
}
```
@@@@

@@@@ slide
### Right Covariant

Common part from Divariant and Bicovariant

```scala
trait RightCovariant[<=>[_, +_]] {
  def rightMap[A, B, C](f: B => C): (A <=> B) => (A <=> C)
}

trait Divariant[:=>[-_, +_]] extends RightCovariant[:=>] {
  def leftContramap[A,B,C](f: C=>A): A:=>B => C:=>B
  def dimap[A,B,C,D](f: C=>A, g: B=>D): A:=>B => C:=>D
}

trait Bicovariant[<=>[+_, +_]] extends RightCovariant[<=>] {
  def leftMap[A, B, AA](f: A=>AA): A<=>B => AA<=>B
  def bimap[A, B, AA, BB](f: A=>AA, g: B=>BB): A<=>B => AA<=>BB
}
```
@@@@

@@@