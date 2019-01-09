package io.chthonic.igdb.poc.business.service

import android.content.Context
import android.support.v4.util.LruCache
import io.chthonic.igdb.poc.data.client.RestClient
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.data.rest.IgdbApi
import io.chthonic.igdb.poc.data.rest.IgdbApicalypseMapping
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by jhavatar on 3/25/2018.
 */
class IgdbService(appContext: Context) {

    companion object {
        private const val BASE_URL = "https://api-v3.igdb.com/"
        private val DEFAULT_HEADERS = mapOf(Pair("user-key", "e35f126c2ae8a127b7696628d0d38260"))
        const val PAGE_SIZE = 20
    }

    private val coverImageCache by lazy {
        LruCache<Long, IgdbImage>(500)
    }

    private val restClient: RestClient by lazy {
        RestClient(BASE_URL, DEFAULT_HEADERS, appContext)
    }

    private val igdbApi: IgdbApi by lazy {
        restClient.restAdapter.create(IgdbApi::class.java)
    }

    private val igdbApicalypse: IgdbApicalypseMapping by lazy {
        IgdbApicalypseMapping(igdbApi)
    }

    fun fetchMostPopularGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApicalypse.getMostPopularGames(page*PAGE_SIZE, PAGE_SIZE)
    }

    fun fetchHighestUserRatedGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApicalypse.getHighestUserRatedGames(page*PAGE_SIZE, PAGE_SIZE)
    }

    fun fetchHighestCriticRatedGames(page: Int = 0): Single<List<IgdbGame>> {
        return igdbApicalypse.getHighestCriticRatedGames(page*PAGE_SIZE, PAGE_SIZE)
    }

    fun fetchCoverImages(idList: List<Long>): Single<List<IgdbImage>> {
        return igdbApicalypse.getCoverImages(idList)
    }

    fun fetchCoverImagesOptimized(gameList: List<IgdbGame>): Single<Map<Long, IgdbImage>> {
        val coverIds: List<Long> = gameList.mapNotNull {
            it.cover
        }
        Timber.d("coverIds = $coverIds")
        val cachedImagesMap = coverIds.mapNotNull {
            val img = coverImageCache.get(it)
            if (img != null) {
                coverImageCache.put(it, img) // re-cache
                it to img

            } else {
                null
            }

        }.toMap()
        Timber.d("cachedImagesMap size = ${cachedImagesMap.size}")

        return if (cachedImagesMap.size == coverIds.size) {
            Single.fromCallable{
                cachedImagesMap.toMap()
            }

        } else {

            val missingCoverIds = coverIds.filter {
                !cachedImagesMap.containsKey(it)
            }
            Timber.d("missingCoverIds = $missingCoverIds")

            fetchCoverImages(missingCoverIds).map { coverList ->

                val fetchedImagesMap = coverList.map {
                    coverImageCache.put(it.id, it) // cache
                    it.id to it
                }.toMap()
                Timber.d("fetchedImagesMap size = ${fetchedImagesMap.size}")

                val imagesMap = fetchedImagesMap.toMutableMap()
                imagesMap.putAll(cachedImagesMap)

                imagesMap.toMap()
            }
        }
    }
}