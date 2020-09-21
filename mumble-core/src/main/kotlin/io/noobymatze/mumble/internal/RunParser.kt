package io.noobymatze.mumble.internal

import io.noobymatze.mumble.ParseResult
import io.noobymatze.mumble.Parser
import io.noobymatze.mumble.Problem
import java.util.*
import kotlin.math.min


@Suppress("UNCHECKED_CAST")
internal class RunParser<out E, out A>(
    private val parser: Parser<E, A>,
    private val input: String,
) {

    private var offset: Int = 0
    private val stack = Stack<(Any?) -> Parser<Any?, Any?>>()

    fun run(): ParseResult<E, A> {
        var curParser: Parser<Any?, Any?> = parser
        while (true) {
            when (curParser) {
                is Parser.Success -> {
                    curParser = nextInstr(curParser.value)
                        ?: return ParseResult.Success(curParser.value as A)
                }

                is Parser.Failure -> {
                    unwindStack()

                    curParser = nextInstr(curParser.errors)
                        ?: return ParseResult.Failure(curParser.errors as Set<Problem<E>>)
                }

                is Parser.FlatMap<*, *, *, *> -> {
                    stack.push(curParser.mapper as (Any?) -> Parser<Any?, Any?>)
                    curParser = curParser.parser
                }

                is Parser.Advance -> {
                    val n = curParser.n
                    val newOffset = min(offset + n, input.length)
                    val result = input.substring(offset, newOffset)

                    offset = newOffset
                    curParser = Parser.Success(result)
                }

                is Parser.TakeWhile -> {
                    val start = offset
                    val p = curParser.predicate
                    while (offset < input.length && p(input[offset])) {
                        offset++
                    }

                    val result = input.substring(start, offset)
                    curParser = Parser.Success(result)
                }

                is Parser.Ensure -> {
                    val n = curParser.n
                    curParser = if (offset + n >= input.length)
                        Parser.Failure(setOf(Problem.unexpectedEof(offset)))
                    else
                        Parser.succeed(Unit)
                }

                is Parser.GetPos -> {
                    curParser = Parser.succeed(offset)
                }

                is Parser.Fold<*, *, *, *> -> {
                    stack.push(curParser as (Any?) -> Parser<Any?, Any?>)
                    curParser = curParser.parser
                }
            }
        }
    }

    private fun nextInstr(value: Any?): Parser<Any?, Any?>? =
        if (stack.isEmpty())
            null
        else
            stack.pop().invoke(value)

    private fun unwindStack() {
        var done = false
        while (!stack.isEmpty() && !done) {
            when (val next = stack.pop()) {
                is Parser.Fold<*, *, *, *> -> {
                    stack.push(next.onError as (Any?) -> Parser<Any?, Any?>)
                    done = true
                }

                else ->
                    Unit
            }
        }
    }

}