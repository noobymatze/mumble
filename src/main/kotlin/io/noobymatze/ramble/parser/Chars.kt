package io.noobymatze.ramble.parser

import io.noobymatze.ramble.Parser
import io.noobymatze.ramble.Problem


interface Chars {

    /**
     * Returns a [Parser], which consumes all characters the match the given [predicate].
     *
     * Be cautious, this parser never fails. If there are no characters to consumer anymore,
     * it will just stop.
     *
     * @param predicate a predicate
     * @return a new [Parser]
     */
    fun takeWhile(predicate: (Char) -> Boolean): Parser<Nothing, String> =
        Parser.TakeWhile(predicate)

    /**
     * Returns a [Parser], which consumes at most [n] tokens.
     *
     * Be cautious, this parser never fails. If it reaches the end of
     * input, it will not take the full number of tokens.
     *
     * @param n the number of tokens to consume
     * @return a new [Parser]
     */
    fun take(n: Int): Parser<Nothing, String> =
        Parser.advance(n)

    /**
     * Returns a [Parser], which matches exactly the given [value].
     *
     * @param value a value
     * @return a new [Parser]
     */
    fun string(value: String): Parser<Nothing, String> =
        take(value.length) flatMap { parsed ->
            if (parsed == value)
                Parser.succeed(parsed)
            else
                Parser.fail(Problem.Item.Unexpected(value, parsed))
        }

}
