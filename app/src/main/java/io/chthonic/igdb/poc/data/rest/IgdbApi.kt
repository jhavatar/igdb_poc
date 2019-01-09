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
    companion object {
        private const val PARAM_REQUIRED_GAME_FIELDS = "id,slug,name,summary,popularity,rating,aggregated_rating,total_rating,cover,first_release_date"
        private const val PARAM_REQUIRED_IMAGE_FIELDS = "*"//id,alpha_channel,animated,height,image_id,url,width"
    }

    @POST("games/")
    fun getGames(@Body body: RequestBody): Single<List<IgdbGame>>

//    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=rating:desc")
//    fun getHighestUserRatedGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>
//
//    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=aggregated_rating:desc")
//    fun getHighestCriticRatedGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>

//    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=rating:desc")
//    fun getHighestUserRatedGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>
//
//    @GET("games/?fields=$PARAM_REQUIRED_GAME_FIELDS&order=aggregated_rating:desc")
//    fun getHighestCriticRatedGames(@Query("offset") offset: Int = 0, @Query("limit") limit: Int = 10): Single<List<IgdbGame>>

    //@GET("covers?fields=$PARAM_REQUIRED_IMAGE_FIELDS")
//    @GET("covers/{idList}/?fields=$PARAM_REQUIRED_IMAGE_FIELDS")
//    fun getCoverImage(@Path("idList") id: String, @Query("limit") limit: Int = 1): Single<List<IgdbImage>>

    @POST("covers/")
    fun getCoverImages(@Body body: RequestBody): Single<List<IgdbImage>>
}