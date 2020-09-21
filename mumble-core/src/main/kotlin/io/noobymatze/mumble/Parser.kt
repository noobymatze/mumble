package io.noobymatze.mumble

import io.noobymatze.mumble.internal.RunParser
import io.noobymatze.mumble.parser.Chars
import io.noobymatze.mumble.parser.andThen
import java.io.Serializable


/**
 * A [Parser] that does not have a custom error.
 */
typealias UParser<A> = Parser<Nothing, A>

/**
 * A [Parser] is a function from strings to a list of pairs of strings
 * and things.
 *
 * You can think of a parser as a function of the following signature
 *
 * ```kotlin
 * (String) -> Result<Set<Problem<E>>, A>
 * ```
 */
sealed class Parser<out E, out A>: Serializable {

    internal data class Success<A>(
        val value: A,
    ): Parser<Nothing, A>()

    internal data class Failure<E>(
        val errors: Set<Problem<E>>,
    ): Parser<E, Nothing>()

    internal data class FlatMap<E: E1, out E1, A, out B>(
        val parser: Parser<E1, A>,
        val mapper: (A) -> Parser<E1, B>,
    ): Parser<E1, B>()

    internal data class Fold<E: E1, out E1, A, out B>(
        val parser: Parser<E, A>,
        val onSuccess: (A) -> Parser<E1, B>,
        val onError: (Set<Problem<E>>) -> Parser<E1, B>,
    ): Parser<E1, B>(), (A) -> Parser<E1, B> {
        override fun invoke(value: A): Parser<E1, B> =
            onSuccess(value)
    }

    internal data class Ensure(
        val n: Int,
    ): Parser<Nothing, Unit>()

    internal data class Advance(
        val n: Int,
    ): Parser<Nothing, String>()

    internal data class TakeWhile(
        val predicate: (Char) -> Boolean
    ): Parser<Nothing, String>()

    internal data class Peek<out E, out A>(
        val parser: Parser<E, A>,
    ): Parser<E, A>()

    internal object GetPos: Parser<Nothing, Int>()

    /**
     *
     * @param f
     * @return
     */
    fun <B> map(f: (A) -> B): Parser<E, B> = when (this) {
        is Success ->
            Success(f(value))

        is Failure ->
            this

        else ->
            FlatMap(this) { Success(f(it)) }
    }

    /**
     * Returns the [ParseResult] of running this [Parser] on the given
     * [input].
     *
     * @param input text to be parsed
     * @return a new [Parser]
     */
    fun run(input: String): ParseResult<E, A> =
        RunParser(this, input).run()

    companion object: Chars {

        /**
         * Returns a new [Parser], always succeeding with the given [value].
         *
         * @param value any value
         * @return a new [Parser]
         */
        fun <A> succeed(value: A): UParser<A> =
            Success(value)

        /**
         * Returns a new [Parser], always failing with the given [problem].
         *
         * @param problem a problem, that occurred
         * @return a new [Parser]
         */
        fun <E> fail(problem: Problem.Reason<E>): Parser<E, Nothing> =
            GetPos andThen {
                Failure(setOf(Problem(it, problem)))
            }

        /**
         * Returns a new [Parser], always failing with the given [error].
         *
         * This is very useful, when you want to use custom errors.
         *
         * @param error any error
         * @return a new [Parser]
         */
        fun <E> fail(error: E): Parser<E, Nothing> =
            fail(Problem.Reason.Custom(error))


        /**
         * Returns a new [Parser], which will ensure, that at least [n]
         * tokens (characters for now) are still available, otherwise
         * will fail.
         *
         * @param n the number of remaining tokens
         * @return a new [Parser]
         */
        internal fun ensure(n: Int): UParser<Unit> =
            Ensure(n)

        /**
         * Returns a new [Parser], which will advance by [n] tokens.
         *
         * @param n the number of remaining tokens
         * @return a new [Parser]
         */
        internal fun advance(n: Int): UParser<String> =
            Advance(n)

    }

}
