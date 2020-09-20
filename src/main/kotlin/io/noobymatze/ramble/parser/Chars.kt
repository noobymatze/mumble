package io.noobymatze.ramble.parser

import io.noobymatze.ramble.Parser
import io.noobymatze.ramble.Problem


interface Chars {

    /**
     *
     * @param predicate
     * @return
     */
    fun takeWhile(predicate: (Char) -> Boolean): Parser<Nothing, String> =
        Parser.TakeWhile(predicate)

    /**
     *
     * @param n
     * @return
     */
    fun take(n: Int): Parser<Nothing, String> =
        Parser.advance(n)

    /**
     *
     * @param value
     * @return
     */
    fun string(value: String): Parser<Nothing, String> =
        take(value.length) flatMap { parsed ->
            if (parsed == value)
                Parser.succeed(parsed)
            else
                Parser.fail(Problem.Item.Unexpected(value, parsed))
        }

}
