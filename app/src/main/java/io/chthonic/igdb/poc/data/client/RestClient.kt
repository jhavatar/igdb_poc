package io.chthonic.igdb.poc.data.client

import android.content.Context
import io.chthonic.igdb.poc.utils.NetUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by jhavatar on 3/14/17.
 */
class RestClient(baseUrl: String, defaultHeaderMap: Map<String, String> = mapOf(), appContext: Context) {

    val restAdapter: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(if (io.chthonic.igdb.poc.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(appContext.cacheDir, cacheSize)

        val okHttpClient = OkHttpClient.Builder()
                .cache(myCache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (NetUtils.isOnline(appContext)) {
                        request.newBuilder().header("Cache-Control", "public, max-age=${5}").build()

                    } else {
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=${60 * 60 * 24 * 7}").build()
                    }
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .addInterceptor(object: Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val builder = chain.request().newBuilder()
                        defaultHeaderMap.forEach{
                            builder.addHeader(it.key, it.value)
                        }
                        return chain.proceed(builder.build())
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

        Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

}