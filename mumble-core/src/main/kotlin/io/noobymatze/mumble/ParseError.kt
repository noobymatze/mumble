package io.noobymatze.mumble

import java.io.Serializable


/**
 * A [ParseError] defines an error, which happened while parsing.
 *
 *
 * ## Note on naming
 *
 * This class could have been called Error, but has not to avoid
 * any naming conflicts with the Kotlin [Error].
 *
 * @param position the position at which the problem has occurred.
 *                 This can be used to pretty print errors.
 * @param reason the actual reason for the [ParseError]
 */
data class ParseError<out E>(
    val position: Int,
    val reason: Reason<E>,
): Serializable {

    sealed class Reason<out E> {

        data class Unexpected(
            val expected: String,
            val found: String? = null
        ): Reason<Nothing>()

        object EndOfInput: Reason<Nothing>()

        data class Custom<out E>(
            val error: E
        ): Reason<E>()

    }

    /**
     *
     * @param f
     * @return
     */
    fun <E1> map(f: (E) -> E1): ParseError<E1> = when (reason) {
        is Reason.Custom ->
            ParseError(position, Reason.Custom(f(reason.error)))

        else ->
            @Suppress("UNCHECKED_CAST")
            this as ParseError<E1>
    }

    companion object {

        /**
         *
         * @param position
         * @param error
         * @return
         */
        fun <E> custom(position: Int, error: E): ParseError<E> =
            ParseError(position, Reason.Custom(error))

        /**
         *
         * @param position
         * @param message
         * @return
         */
        fun <E> unexpected(position: Int, message: String): ParseError<E> =
            ParseError(position, Reason.Unexpected(message))

        /**
         *
         * @param position
         * @return
         */
        fun <E> unexpectedEof(position: Int): ParseError<E> =
            ParseError(position, Reason.EndOfInput)

    }

}
