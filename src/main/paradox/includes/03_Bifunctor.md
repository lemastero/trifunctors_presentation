@@@ slide

@@@@ slide
### Bicovariant

map 2 things we have or can compute (2 * Covariant)
```scala
trait Bicovariant[<=>[+_, +_]] {

  def rightMap[A,B,C](f: B => C): (A<=>B) => (A<=>C)

  def leftMap[A,B,AA](f: A => AA): (A<=>B) => (AA<=>B)

  def bimap[A,B,AA,BB](f: A => AA, g: B => BB):
      (A<=>B) => (AA<=>BB) =
    rightMap(g) andThen leftMap(f)
}
```
@@@@


@@@@ slide
### Bicovariant Laws 1

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

@@@@ slide
### Bicovariant Laws 2

leftMap behaves nicely
```scala
def leftMapCompose[A, B, A2, A3](
  ab: A<=>B, f: A => A2, g: A2 => A3
) = {
  val lh: A<=>B => A3<=>B = leftMap(g compose f)
  val rh: A<=>B => A3<=>B = leftMap(g) compose leftMap(f)
  lh(ab) == rh(ab)
}

def leftMapIdentity[A, B](ab: A <=> B) = {
  leftMap(identity[A])(ab) == ab
}
```
@@@@

@@@@ slide
### Bicovariant Laws 3

bimap behaves nicely
```scala
def bimapCompose[A,B,A2,A3,B2,B3](ab: A<=>B,
  g: A2 => A3, f: A => A2, i: B => B2, h: B2 => B3
) = {
  val lhs: A<=>B => A3<=>B3 = bimap(g compose f, h compose i)

  val rhs1: A<=>B => A2<=>B2   = bimap(f, i)
  val rhs2: A2<=>B2 => A3<=>B3 = bimap(g, h)
  val rhs3: A<=>B => A3<=>B3   = rhs2 compose rhs1
  lhs(ab) == rhs3(ab)
}

def bimapIdentity[A,B,B2,B3](ab: A<=>B) =
  bimap(identity[A], identity[B])(ab) == ab
```
@@@@

@@@@ slide
### Bicovariant Laws 4

bimap works the same as rightMap combined with leftMap
```scala
def bimapCoherence[A, A2, A3, B, B2, B3](
  ab: A <=> B, f: A => A2, g: B => B2
) = {
  val lhs: A <=> B => A2<=>B2   = bimap(f, g)

  val rhs1: A <=> B => A<=>B2   = rightMap(g)
  val rhs2: A <=> B2 => A2<=>B2 = leftMap(f)
  val rhs3: A <=> B => A2<=>B2  = rhs1 andThen rhs2

  lhs(ab) == rhs3(ab)
}
```
@@@@

@@@@ slide
### Bicovariant Instances - Tuple

```scala
implicit val Tuple2Bicovariant: Bicovariant[Tuple2] =
  new Bicovariant[Tuple2] {
    def bimap[A B,AA,BB](f: A => AA, g: B => BB):
         (A,B) => (AA,BB) =
      { case (a, b) => (f(a), g(b)) }
  }
```
@@@@

@@@@ slide
### Bicovariant Instances - Either

Either
```scala
implicit val EitherBicovariant: Bicovariant[Either] =
  new Bicovariant[Either] {
    def bimap[A,B,AA,BB](f: A => AA, g: B => BB):
        Either[A,B] => Either[AA,BB] = {
      case Right(a) => Right(g(a))
      case Left(b)  => Left(f(b))
    }
  }
```
@@@@

@@@