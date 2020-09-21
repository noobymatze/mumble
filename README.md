# Mumble

A zero dependency Kotlin library implementing parser combinators,
inspired by megaparsec, combine and so on.


## Example

Imagine we would like to parse a very simple language for describing
arithmetic expressions.

```clojure
(+ 3 5 6)
```

We can model this example using `sealed class`es in Kotlin. Take a
look.

```kotlin
sealed class Expr
data class Add(val expressions: List<Expr>): Expr()
data class Num(val value: Int): Expr()

val lparen = Parser.string("(")
val rparen = Parser.string(")")
val plus   = Parser.string("+")
val number = Parser.int map Expr::Num

fun add(): UParser<Expr> =
    plus flatMap { expr().many() } map Expr::Add

fun expr(): UParser<Expr> = Parser
    .oneOf(number, add())
    .sepBy(Parser.spaces)
    
val input = "(+ 3 5 6)"
val result = expr().parse(input)

// Add(Num(3), Num(5), Num(6))
```


