package io.noobymatze.mumble

/**
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

}
