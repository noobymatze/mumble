package io.noobymatze.ramble.parser

import io.noobymatze.ramble.Parser
import io.noobymatze.ramble.Problem


/**
 *
 * @param f
 * @return
 */
infix fun <E: E1, E1, A, B> Parser<E, A>.andThen(f: (A) -> Parser<E, B>): Parser<E1, B> =
    Parser.FlatMap(this, f)


/**
 *
 * @param onSuccess
 * @param onFailure
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
 *
 */
infix fun <E: E1, E1, A: A1, A1> Parser<E1, A1>.recover(handler: (Set<Problem<E1>>) -> Parser<E, A>): Parser<E1, A1> =
    fold(
        onSuccess = { Parser.succeed(it) },
        onFailure = handler
    )

/**
 *
 */
infix fun <E: E1, E1, A: A1, A1> Parser<E1, A1>.orElse(parser: Parser<E, A>): Parser<E1, A1> =
    recover { parser }
