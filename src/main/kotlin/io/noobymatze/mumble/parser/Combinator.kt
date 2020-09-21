package io.noobymatze.mumble.parser

import io.noobymatze.mumble.Parser
import io.noobymatze.mumble.Problem


/**
 * Returns a [Parser], which runs this parser and uses the resulting, successful
 * value to run the next [Parser].
 *
 * This function is very useful to chain multiple parsers together.
 *
 * @param f a function to be applied to a successful value
 * @return a new [Parser]
 */
infix fun <E: E1, E1, A, B> Parser<E, A>.andThen(f: (A) -> Parser<E, B>): Parser<E1, B> = when (this) {
    is Parser.Success ->
        f(value)

    is Parser.Failure ->
        this

    else ->
        Parser.FlatMap(this, f)
}

/**
 * Returns a [Parser], which will apply [onSuccess] to a successful value
 * and [onFailure] to an unsuccessful one.
 *
 * This combinator is foundational for a host of other combinators, like [recover]
 * and [orElse].
 *
 * @param onSuccess a function to be applied to a successful value
 * @param onFailure a function to be applied to a failed alue
 * @return a new [Parser]
 */
fun <E: E1, E1, A, B> Parser<E1, A>.fold(
    onSuccess: (A) -> Parser<E, B>,
    onFailure: (Set<Problem<E1>>) -> Parser<E1, B>
): Parser<E1, B> = when (this) {
    is Parser.Success ->
        onSuccess(value)

    is Parser.Failure ->
        onFailure(errors)

    else ->
        Parser.Fold(this, onSuccess, onFailure)
}

/**
 * Returns a [Parser], which may or may not recover from the given errors.
 *
 * @param handler a function applied to a set of errors
 * @return a new [Parser]
 */
infix fun <E: E1, E1, A: A1, A1> Parser<E1, A1>.recover(handler: (Set<Problem<E1>>) -> Parser<E, A>): Parser<E1, A1> =
    fold(
        onSuccess = { Parser.succeed(it) },
        onFailure = handler
    )

/**
 * Returns a [Parser], which runs this parser first and tries [parser] on
 * any errors.
 *
 * @param parser another parser to be run, when this parser fails
 * @return a new [Parser]
 */
infix fun <E: E1, E1, A: A1, A1> Parser<E1, A1>.orElse(parser: () -> Parser<E, A>): Parser<E1, A1> =
    recover { parser() }
