@@@ slide

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

@@@