@@@ slide { color=#222222 }

@@@@ slide
### 2 type parameters ZOO (1/2)

Separate abstractions for mapLeft, map, contramap

```scala
trait RightCovariant[P[_, +_]] {
  def map[A,B,C](fa: P[A,B])(f: B => C): P[A,C]
}

trait LeftCovariant[P[+_, _]] {
  def mapLeft[A,AA,B](fa: P[A,B])(g: A => AA): P[AA,B]
}

trait LeftContravariant[P[-_, _]] {
  def contramap[A,AA,B](fa: P[A,B])(f: AA => A): P[AA,B]
}
```
@@@@

@@@@ slide
### 2 type parameters ZOO (2/2)

Separate abstractions for mapLeft, map, contramap

```scala
trait Divariant[P[-_, +_]]
    extends RightCovariant[P] with LeftContravariant[P] {
  def dimap[A,B,C,D](fa: P[A,B])(f: C=>A, g: B=>D): P[C,D]
}

trait Bicovariant[P[+_, +_]]
    extends RightCovariant[P] with LeftCovariant[P] {
  def bimap[A,B,AA,BB](fa: P[A,B])(f: A=>AA, g: B=>BB): P[AA,BB]
}
```

YAGNI ?

@@@@

@@@@ slide
### More info about abstractions with 2 type params

* Experiments with abstractions: https://github.com/lemastero/Triglav  
* PR zio-prelude adding Bicovariant and RightFunctor https://github.com/zio/zio-prelude/pull/279  
* PR zio-prelude adding Divariant laws https://github.com/zio/zio-prelude/pull/293
@@@@

@@@