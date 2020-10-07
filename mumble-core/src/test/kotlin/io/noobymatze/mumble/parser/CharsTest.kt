package io.noobymatze.mumble.parser

import io.noobymatze.mumble.ParseResult
import io.noobymatze.mumble.Parser
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import kotlin.test.assertEquals
import kotlin.test.fail


class CharsTest {

    @Property
    fun stringShouldParseExactly(@ForAll input: String) {
        val parser = Parser.string(input)
        val result = parser.parse(input)
        assertEquals(ParseResult.Success(input), result)
    }

    @Property
    fun takeWhileShouldTakeLetters(@ForAll input: String) {
        val x = Parser.takeWhile { it.isLetter() }
        val result = input.takeWhile { it.isLetter() }
        assertParses(result, x, input)
    }

    @Example
    fun takeWhileWorks() {
        val parser = Parser.takeWhile { it == 'a' }
        assertParses("aaa", parser, "aaa b")
    }

    private fun <E> assertParses(expected: String, parser: Parser<E, String>, input: String) {
        val run = parser.parse(input)
        run.fold(
            onSuccess = { assertEquals(expected, it) },
            onError = { fail("Failed with error $it") }
        )
    }

}