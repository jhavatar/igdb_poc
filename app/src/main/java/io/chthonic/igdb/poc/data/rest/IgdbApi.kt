package io.chthonic.igdb.poc.data.rest

import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by jhavatar on 3/25/2018.
 */
interface IgdbApi {

    @POST("games/")
    fun getGames(@Body body: RequestBody): Single<List<IgdbGame>>

    @POST("covers/")
    fun getCoverImages(@Body body: RequestBody): Single<List<IgdbImage>>
}