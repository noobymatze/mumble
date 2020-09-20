package io.noobymatze.ramble.parser

import io.noobymatze.ramble.Parser
import io.noobymatze.ramble.Problem


/**
 *
 * @param f
 * @return
 */
infix fun <E: E1, E1, A, B> Parser<E, A>.flatMap(f: (A) -> Parser<E, B>): Parser<E1, B> =
    Parser.FlatMap(this, f)


/**
 *
 * @param onSuccess
 * @param onFailure
 */
fun <E: E1, E1, A, B> Parser<E, A>.fold(
    onSuccess: (A) -> Parser<E1, B>,
    onFailure: (Set<Problem<E>>) -> Parser<E1, B>
): Parser<E1, B> = when (this) {
    is Parser.Success ->
        onSuccess(value)

    is Parser.Failure ->
        onFailure(errors)

    else ->
        Parser.Fold(this, onSuccess, onFailure)
}
