package io.chthonic.igdb.poc.data.rest

import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.TickerLot
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Created by jhavatar on 3/25/2018.
 */
interface IgdbApi {
    companion object {
        private const val PARAM_REQUIRED_GAME_FIELDS = "id,slug,name,summary,popularity,total_rating,cover,first_release_date"
    }

    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=popularity:desc")
    fun getMostPopularGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>

    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=rating:desc")
    fun getHighestRatestGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>
}