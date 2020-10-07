package io.noobymatze.mumble

/**
 * The [ParseResult] represents the result of parsing some input according to
 * a [Parser]. It can either be ok or have failed.
 *
 *
 *
 */
sealed class ParseResult<out E, out A> {

    internal data class Success<out A>(
        val value: A,
    ): ParseResult<Nothing, A>()

    internal data class Failure<out E>(
        val errors: Set<ParseError<E>>,
    ): ParseResult<E, Nothing>()

    /**
     *
     * @param f
     * @return
     */
    fun <B> map(f: (A) -> B): ParseResult<E, B> = when (this) {
        is Success ->
            Success(f(value))

        is Failure ->
            this
    }

    /**
     *
     * @param onSuccess
     * @param onError
     * @return
     */
    fun <B> fold(
        onSuccess: (A) -> B,
        onError: (Set<ParseError<E>>) -> B
    ): B = when (this) {
        is Success ->
            onSuccess(value)

        is Failure ->
            onError(errors)
    }

    /**
     * Returns a value of type [A] or `null`, if an error has occurred during
     * while running a [Parser].
     *
     * @return the parsed value, if there is one.
     */
    fun getOrNull(): A? = fold(
        onSuccess = { it },
        onError = { null }
    )

    companion object {

        fun <A> succeed(value: A): ParseResult<Nothing, A> =
            Success(value)

    }

}
