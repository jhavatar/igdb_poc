package io.chthonic.igdb.poc.data.rest

import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * Created by jhavatar on 12/30/2018.
 */
class IgdbApicalypseMapping(private val igdbApi: IgdbApi) {

    companion object {
        private const val PARAM_REQUIRED_GAME_FIELDS = "slug,name,summary,popularity,rating,aggregated_rating,total_rating,cover,first_release_date"
        private const val PARAM_REQUIRED_IMAGE_FIELDS = "*"//id,alpha_channel,animated,height,image_id,url,width"
    }

    fun getMostPopularGames(offset: Int = 0, limit: Int = 10): Single<List<IgdbGame>> {
        val body = """fields $PARAM_REQUIRED_GAME_FIELDS;
            offset $offset;
            limit $limit;
            where popularity > 0;
            sort popularity desc;""".trimIndent().trimStart()

        return igdbApi.getGames(RequestBody.create(MediaType.get("text/plain") , body))
    }

    fun getHighestUserRatedGames(offset: Int = 0, limit: Int = 10): Single<List<IgdbGame>> {
        val body = """fields $PARAM_REQUIRED_GAME_FIELDS;
            offset $offset;
            limit $limit;
            where rating > 0;
            sort rating desc;""".trimIndent().trimStart()

        return igdbApi.getGames(RequestBody.create(MediaType.get("text/plain") , body))
    }

    fun getHighestCriticRatedGames(offset: Int = 0, limit: Int = 10): Single<List<IgdbGame>> {
        val body = """fields $PARAM_REQUIRED_GAME_FIELDS;
            offset $offset;
            limit $limit;
            where aggregated_rating > 0;
            sort aggregated_rating desc;""".trimIndent().trimStart()

        return igdbApi.getGames(RequestBody.create(MediaType.get("text/plain") , body))
    }


    fun getCoverImages(idList: List<Long>): Single<List<IgdbImage>> {
        // NB, limit is required since there is a default value that may be smaller than requested size
        val body = """fields *;
            where id = (${idList.sorted().joinToString(separator = ",")});
            offset 0;
            limit ${idList.size};""".trimIndent().trimStart()

        return igdbApi.getCoverImages(RequestBody.create(MediaType.get("text/plain") , body))
    }
}