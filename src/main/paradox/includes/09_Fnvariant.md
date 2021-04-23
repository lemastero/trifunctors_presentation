@@@ slide { color=#222222 }

@@@@ slide

### Friends: Fnvariant

@@@@

@@@@ slide

### Fnvariant

```scala
trait Fnvariant[Z[-_, -_, +_]] {

  def fnmap[R,E,A,R1,E1,A1](r: R1 => R, e: E1 => E, a: A => A1
  ): Z[R,E,A] => Z[R1,E1,A1]

  def dimap[R,E,A,R1,A1](r: R1 => R, a: A => A1
  ): Z[R,E,A] => Z[R1,E,A1]

  def righContramap[R,E,A,E1](r: E1 => E): Z[R,E,A] => Z[R,E1,A]
}
```

@@@@

@@@@ slide

### Fnvariant insance for Function2

```scala
new Fnvariant[Function2] {
  def fnmap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E1 => E, a: A => A1
  ): ((R,E) => A) => (R1,E1) => A1 =
    rea => { case (r1, e1) =>
      val fr: R = r(r1)
      val fe: E = e(e1)
      a(rea(fr, fe))
    }
}
```

@@@@

@@@@ slide

### Fnvariant insance for Schedule

```scala
new Fnvariant[Schedule] {
  def fnmap[R,E,A,R1,E1,A1](
    r: R1 => R, e: E1 => E, a: A => A1
  ): Schedule[R,E,A] => Schedule[R1,E1,A1] =
    s => s.dimap(e,a).provideSome(r)
}
```

@@@@

@@@@ slide

### Friends: Trivariant

Hierarchy in Haskell:

```scala
trait Covariant[Z[+_]]
trait Bicovariant[B[+_, +_]]
trait Trivariant[Z[+_, +_, +_]]
```

@@@@

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

### Tuple3 Trivariant

```scala
new Trivariant[Tuple3] {
  def trimap[R,E,A,R1,E1,A1](
    r: R => R1, e: E => E1, a: A => A1
  ): (R,E,A) => (R1,E1,A1) = {
    case (fr,fe,fa) => (r(fr), e(fe), a(fa))
  }
}
```

* Tuple4, Tuple5, ...., Tuple22

@@@@

@@@@ slide

### Beyoned Zivariant, Trivariant, Fnvariant

Do we need abstraction for?

@@@@@ fragments
1. CT[-_, -_, -_] ?
2. F[+_, -_, -_] ?
3. F[-_, +_, -_] F[-_, +_, -_] ?
4. Z[+_, -_, +_] Z[+_, -_, +_] ?
@@@@@

@@@@

@@@