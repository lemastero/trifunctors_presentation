@@@ slide

@@@@ slide

### Trivariant

```scala
trait Trivariant[Z[+_, +_, +_]] {
  def mapFirst[R,E,A,R1](e: R => R1): Z[R,E,A] => Z[R1,E,A]
  def map[R,E,A,A1](a: A => A1): Z[R,E,A] => Z[R,E,A1]
  def mapLeft[R,E,A,E1](e: E => E1): Z[R,E,A] => Z[R,E1,A]
  
  def bimap[R,E,A,E1,A1](e: E => E1, a: A => A1
  ): Z[R,E,A] => Z[R,E1,A1]

  def trimap[R,E,A,R1,E1,A1](r: R => R1, e: E => E1, a: A => A1
  ): Z[R,E,A] => Z[R1,E1,A1]
}
```

@@@@

@@@@ slide

### Trivariant

```scala
new Trivariant[Tuple3] {
  def trimap[R,E,A,R1,E1,A1](
    r: R => R1, e: E => E1, a: A => A1
  ): (R,E,A) => (R1,E1,A1) = {
    case (fr,fe,fa) => (r(fr), e(fe), a(fa))
  }
}
```

@@@@

@@@