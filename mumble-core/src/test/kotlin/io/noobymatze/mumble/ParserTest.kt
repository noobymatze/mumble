package io.noobymatze.mumble

import net.jqwik.api.*
import kotlin.test.assertEquals


class ParserTest {

    @Property
    fun identityLaw(@ForAll("stringParsers") a: Parser<Any?, *>,) {
        assertEquals(
            a.map { it }.run(""),
            a.run("")
        )
    }

    @Property
    fun compositionLaw(
        @ForAll("stringParsers") parser: Parser<Any?, String>,
        @ForAll("f") f: (String) -> Any?,
        @ForAll("g") g: (String) -> String,
    ) {
        val a = parser.map(g).map(f)
        val b = parser.map { f(g(it)) }
        assertEquals(a.run(""), b.run(""))
    }

    @Provide
    fun f(): Arbitrary<(String) -> Any?> {
        return Arbitraries.of({ a -> a.length + 3 })
    }

    @Provide
    fun g(): Arbitrary<(String) -> String> {
        return Arbitraries.of({ a -> a + "6" })
    }

    @Provide
    fun stringParsers(): Arbitrary<Parser<Any?, Any?>> {
        return Arbitraries.strings().flatMap { value ->
            Arbitraries.randomValue { random ->
                if (random.nextBoolean())
                    Parser.succeed(value)
                else
                    Parser.fail(value)
            }
        }
    }

}