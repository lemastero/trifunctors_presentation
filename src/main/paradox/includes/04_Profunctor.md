@@@ slide

@@@@ slide
### Divariant

Generalized function (prepare input - leftMap, map output - rightMap)

```scala
trait Divariant[:=>[-_, +_]] {

  def rightMap[A,B,C](f: B => C): (A :=> B) => (A :=> C)

  def leftContramap[A,B,C](f: C => A): (A :=> B) => (C :=> B)

  def dimap[A,B,C,D](f: C => A, g: B => D):
      (A :=> B) => (C :=> D) =
    (ab: A :=> B) => rightMap(g)(leftContramap(f)(ab))
}
```
@@@@

@@@@ slide
### Divariant Laws 1

rightMap behaves nicely
```scala
def rightMapCompose[A, B, B2, B3](
  ab: A:=>B, f: B => B2, g: B2 => B3
) = {
  val lh: A:=>B => A:=>B3 = rightMap(g compose f)
  val rh: A:=>B => A:=>B3 = rightMap(g) compose rightMap(f)
  lh(ab) == rh(ab)
}

def rightMapIdentity[A, B](ab: A:=>B) = {
  rightMap(identity[B])(ab) == ab
}
```
@@@@

@@@@ slide
### Divariant Laws 2

leftContramap behaves nicely

```scala
def leftContramapCompose[A, B, A2, A3](
  ab: A :=> B, f: A2 => A, g: A3 => A2
) = {
  val lhs: A:=>B => A3:=>B = lContramap(f compose g)
  val rhs: A:=>B => A3:=>B = lContramap(g) compose lContramap(f)
  lhs(ab) == rhs(ab)
}

def leftContramapidentity[A, B](ab: A :=> B) =
  lContramap(identity[A])(ab) == ab
```
@@@@

@@@@ slide
### Divariant Laws 3

dimap behaves nicely

```scala
def dimapCompose[A,B,A2,A3,B2,B3](
  ab: A :=> B, g: A3 => A2, f: A2 => A, i: B => B2, h: B2 => B3
) = {
  val fg: A3 => A               = f compose g
  val hi: B => B3               = h compose i
  val lhs: A :=> B => A3 :=> B3 = dimap(fg, hi)

  val rhs1: A :=> B => A2 :=> B2   = dimap(f, i)
  val rhs2: A2 :=> B2 => A3 :=> B3 = dimap(g, h)
  val rhs3: A :=> B => A3 :=> B3   = rhs2 compose rhs1
  lhs(ab) == rhs3(ab)
}

def dimapIdentity[A,B,B2,B3](ab: A :=> B) =
  dimap(identity[A], identity[B])(ab) == ab
```
@@@@

@@@@ slide
### Divariant Laws 3

dimap behaves as rightMap followed by leftContramap 

```scala
def dimapCoherence[A, A2, A3, B, B2, B3](
  ab: A :=> B, f: A2 => A, g: B => B2
) = {
  val lhs: A :=> B => A2 :=> B2 = dimap(f, g)

  val rhs1: A :=> B => A :=> B2   = rightMap(g)
  val rhs2: A :=> B2 => A2 :=> B2 = leftContramap(f)
  val rhs3: A :=> B => A2 :=> B2  = rhs1 andThen rhs2

  lhs(ab) == rhs3(ab)
}
```
@@@@

@@@@ slide
### Divariant Instance - Functions1

dimap behaves as rightMap followed by leftContramap 

```scala
implicit val Function1Divariant: Divariant[Function1] =
  new Divariant[Function1] {
    def leftContramap[A, B, C](c2a: C => A): (A => B) => C => B = 
      a2b => c2a andThen a2b
    
    def rightMap[A, B, C](b2c: B => C): (A => B) => A => C =
      a2b => b2c compose a2b 
  }
```
@@@@

@@@