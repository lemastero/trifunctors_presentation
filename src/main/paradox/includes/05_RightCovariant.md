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

@@@@ slide
### RightCovariant Laws

rightMap behaves nicely
```scala
def rightMapCompose[A, B, B2, B3](
  ab: A<=>B, f: B => B2, g: B2 => B3
) = {
  val lh: A<=>B => A<=>B3 = rightMap(g compose f)
  val rh: A<=>B => A<=>B3 = rightMap(g) compose rightMap(f)
  lh(ab) == rh(ab)
}

def rightMapIdentity[A, B](ab: A<=>B) = {
  rightMap(identity[B])(ab) == ab
}
```
@@@@

@@@