package io.chthonic.igdb.poc.business.service

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import io.chthonic.igdb.poc.data.client.RestClient
import io.chthonic.igdb.poc.data.model.ExchangeState
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.rest.IgdbApi
import io.reactivex.Single

/**
 * Created by jhavatar on 3/25/2018.
 */
class IgdbService(prefs: SharedPreferences,
                  serializer: JsonAdapter<ExchangeState>) {

    companion object {
        private const val BASE_URL = "https://api-endpoint.igdb.com/"
        private val DEFAULT_HEADERS = mapOf<String, String>(Pair("user-key", "17807173806c0c76d1ed416133058965"))
        const val PAGE_SIZE = 20
    }

    private val restClient: RestClient by lazy {
        RestClient(BASE_URL, DEFAULT_HEADERS)
    }

    private val igdbApi: IgdbApi by lazy {
        restClient.restAdapter.create(IgdbApi::class.java)
    }

    fun fetchMostPopularGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApi.getMostPopularGames(page*PAGE_SIZE, PAGE_SIZE)
    }

    fun fetchHighestUserRatedGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApi.getHighestUserRatedGames(page*PAGE_SIZE, PAGE_SIZE)
    }

    fun fetchHighestCriticRatedGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApi.getHighestCriticRatedGames(page*PAGE_SIZE, PAGE_SIZE)
    }
}