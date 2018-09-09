package io.chthonic.igdb.poc.data.model

/**
 * Created by jhavatar on 9/9/2018.
 */
sealed class Order(val id: Int)  {
    companion object {
        fun fromId(id: Int, fallback: Order): Order {
            return listOf(POPULARITY, USER_REVIEW, CRITIC_REVIEW).firstOrNull() {
                it.id == id
            } ?: fallback
        }
    }

    object POPULARITY: Order(0)
    object USER_REVIEW: Order(1)
    object CRITIC_REVIEW: Order(2)
}