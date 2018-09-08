package io.chthonic.igdb.poc.data.model

/**
 * Created by jhavatar on 9/9/2018.
 */
sealed class Order  {
    object POPULARITY: Order()
    object USER_REVIEW: Order()
    object CRITIC_REVIEW: Order()
}