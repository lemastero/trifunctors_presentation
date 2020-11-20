@@@ slide { color=#242220 }

@@@@ slide

## Contravariant
### prepare the input

@@@@

@@@@ slide

### Validation (1/4)

Simple validation

```scala
case class Validator[-A](isValid: A => Boolean)
case class Balance(amount: BigDecimal)
val balanceOverdrawn = Validator[Balance](_.amount > 0)

balanceOverdrawn.isValid(Balance(List(100))) == false
balanceOverdrawn.isValid(Balance(List(10, -200))) == true
```

@@@@

@@@@ slide

### Validation (2/4)
How to reuse model and validation?

```scala
case class Validator[-A](isValid: A => Boolean)
case class Balance(amount: BigDecimal)
val balanceOverdrawn: Validator[Balance] = ...

case class Account(name: String, payments: List[BigDecimal])
def getAccountBalance(a: Account): Balance = ...

val hasOverdrawnBalance: Validator[Account] =
  balanceOverdrawn ¯\_(ツ)_/¯ getAccountBalance
```

@@@@

@@@@ slide

### Validation (3/4)
Prepare input and then validate:

```scala
case class Validator[-A](isValid: A => Boolean)

def prepare[A, B](
      prepareInput: B=>A,
      validator: Validator[A]): Validator[B] =
  Validator[B](prepareInput andThen validator.isValid)
```

@@@@

@@@@ slide

### Validation (4/4)
Use of prepare validation:

```scala
case class Validator[-A](isValid: A => Boolean)

implicit val validatorContrav: Contravariant[Validator] = ...

def getAccountBalance(a: Account): Balance = ...
val balanceOverdrawn: Validator[Balance] = ...
val hasOverdrawnBalance: Validator[Account] =
  prepare(balanceOverdrawn, getAccountBalance)

hasOverdrawnBalance.isValid(accountAlice) == false
hasOverdrawnBalance.isValid(accountBob) == true
```

@@@@

@@@@ slide

### Serialization (1/4)

Simple serialization to String:

```scala
trait Show[-T] { // serialization
  def show(a: T): String
}

object Show { // helper
  def show[A,B](f: A => String): Show[A] = new Show[A] {
    def show(a: A): String = f(a)
  }
}

case class Money(v: Int)
```

@@@@

@@@@ slide

### Serialization (2/4)

Serialization with type class

```scala
trait Show[-T] { def show(a: T): String }

implicit class ShowSyntax[A](a: A) {
  def show(implicit ST: Show[A]): String = ST.show(a)
}

case class Money(v: Int)

implicit val showMoney: Show[Money] = Show.show(m => s"${m.v}")

Money(42).show == "42"
```

@@@@

@@@@ slide

### Serialization (3/4)
How to reuse?
```scala
trait Show[-T] { def show(a: T): String }
// ...
case class Money(v: Int)
implicit val showMoney: Show[Money] = ???

case class Salary(amount: Money)
implicit val showSalary: Show[Salary] =
  showMoney ¯\_(ツ)_/¯ (_.amount)
```

@@@@

@@@@ slide

### Serialization (4/4)
Prepare and show:

```scala
trait Show[-T] { def show(a: T): String }
// ...
case class Money(v: Int)
implicit val showMoney: Show[Money] = ???
case class Salary(amount: Money)

def prepareAndShow[A, B](ba: B => A, s: Show[A]): Show[B] =
      Show.show(ba andThen s.show)

implicit val showSalary: Show[Salary] =
  prepareAndShow(_.amount)(showMoney)

Salary(Money(42)).show == "42"
```

@@@@

@@@@ slide

### Contravariant = input preparation

```scala
trait Contravariant[F[-_]] {
  def contramap[A, B](f: B => A): F[A] => F[B]
}
```

@@@@

@@@@ slide

### Validation as contravariant

```scala
case class Validator[-A](isValid: A => Boolean)

def prepare[A, B](
      prepareInput: B=>A,
      validator: Validator[A]): Validator[B] =
  Validator[B](prepareInput andThen validator.isValid)


val validatorContravariant: Contravariant[Validator] =
  new Contravariant[Validator] {
    def contramap[A, B](f: B=>A): Validator[A] => Validator[B] =
      validator =>
        Validator[B](f andThen validator.isValid)
  }
```

@@@@

@@@@ slide

### Serialization as Contravariant

```scala
trait Show[-T] { def show(a: T): String }

def prepareAndShow[A, B](ba: B => A, s: Show[A]): Show[B] =
  Show.show(ba andThen s.show)

implicit val showContravariant: Contravariant[Show] =
  new Contravariant[Show] {
    def contramap[A, B](ba: B => A): Show[A] => Show[B] = s =>
      Show.show(ba andThen s.show)
  }
```

@@@@

@@@@ slide

### Use of Contravariant Validator

```scala
case class Validator[-A](isValid: A => Boolean)

implicit val validatorContrav: Contravariant[Validator] = ...

def getAccountBalance(a: Account): Balance = ...
val balanceOverdrawn: Validator[Balance] = ...
val hasOverdrawnBalance: Validator[Account] =
  balanceOverdrawn.contramap(getAccountBalance)

hasOverdrawnBalance.isValid(accountAlice) == false
hasOverdrawnBalance.isValid(accountBob) == true
```

@@@@

@@@@ slide

### Example - Debug

```scala
implicit val showContravariant: Contravariant[Debug] =
  new Contravariant[Debug] {
    def contramap[A, B](ba: B => A): Debug[A] => Debug[B] = s =>
      Debug.make[B](ba andThen s.debug)
  }

case class Money(v: Int)
case class Salary(amount: Money)

implicit val showMoney: Debug[Money] = ???
implicit val showSalary: Debug[Salary] =
  showMoney.contramap(_.amount)

Salary(Money(42)).debug.render mustBe """"42""""
```

@@@@

@@@@ slide

### Example - Ord

Explicit variance = no need to narrow 
```scala
case class Money(amount: Int)
case class Salary(salary: Money)

implicit val mOrd: Ord[Money] = intOrd.contramap(_.amount)
implicit val sOrd: Ord[Salary] = mOrd.contramap(_.salary)

Money(100) < Money(200) == true
```

@@@@

@@@@ slide

### No need for narrow

Explicit variance = no need to narrow 
```scala
class Money(val amount: Int)
class Salary(amount: Int) extends Money(amount)

val mOrd: Ord[Money] = Ord.fromScala[Int].contramap(_.amount)
val salaryOrd: Ord[Salary] = mOrd
salaryOrd.compare(new Salary(42), new Salary(41)) == GreaterThan
```

@@@@

@@@@ slide

### Contravariant laws
Prepare input that behaves nicely:

```scala
object Contravariant
  extends LawfulF.Contravariant[DeriveEq, Equal] {

  val identityLaw: Co1LawsF = new Co1LawsF("identityLaw") {
    def apply[F[-_]: DeriveEq, A: Equal](fa: F[A]) =
      fa.contramap(identity[A]) <-> fa
  }

  val compositionLaw: CoLawsF = new CoLawsF("compositionLaw") {
    def apply[F[-_]: DeriveEq, A: Equal, B: Equal, C: Equal](
          fa: F[A], f: B => A, g: C => B) =
      fa.contramap(f).contramap(g) <-> fa.contramap(f compose g)
  }
}
```

@@@@

@@@@ slide

### Example - ZIO

ZIO contramap = provideSome

```scala
val loadEmployeeFromDb: ZIO[Int, Throwable, Employee] = ???

def idFromEmployee: EmployeeId => Int = ???

val loadEmployee1: ZIO[EmployeeId, Throwable, Employee] =
  loadEmployeeFromDb.provideSome(idFromEmployee)
val loadEmployee2: ZIO[EmployeeId, Throwable, Employee] =
  loadEmployeeFromDb.contramap(idFromEmployee)
```

@@@@

@@@