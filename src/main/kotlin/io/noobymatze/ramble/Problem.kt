package io.noobymatze.ramble

import java.io.Serializable


/**
 *
 * @param position
 * @param item
 */
data class Problem<out E>(
    val position: Int,
    val item: Item<E>,
): Serializable {

    sealed class Item<out E> {

        data class Unexpected(
            val expected: String,
            val found: String? = null
        ): Item<Nothing>()

        object Eof: Item<Nothing>()

        data class Custom<out E>(
            val error: E
        ): Item<E>()

    }

    /**
     *
     * @param f
     * @return
     */
    fun <E1> map(f: (E) -> E1): Problem<E1> = when (item) {
        is Item.Custom ->
            Problem(position, Item.Custom(f(item.error)))

        else ->
            @Suppress("UNCHECKED_CAST")
            this as Problem<E1>
    }

    companion object {

        /**
         *
         * @param position
         * @param error
         * @return
         */
        fun <E> custom(position: Int, error: E): Problem<E> =
            Problem(position, Item.Custom(error))

        /**
         *
         * @param position
         * @param message
         * @return
         */
        fun <E> unexpected(position: Int, message: String): Problem<E> =
            Problem(position, Item.Unexpected(message))

        /**
         *
         * @param position
         * @return
         */
        fun <E> unexpectedEof(position: Int): Problem<E> =
            Problem(position, Item.Eof)

    }

}
