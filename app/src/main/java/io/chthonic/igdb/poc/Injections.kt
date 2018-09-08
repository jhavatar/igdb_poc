package io.chthonic.igdb.poc

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.chthonic.igdb.poc.business.service.IgdbService
import io.chthonic.igdb.poc.data.model.CalculatorSerializableState
import io.chthonic.igdb.poc.data.model.ExchangeState

/**
 * Created by jhavatar on 4/2/2018.
 */
fun depInject(app: Application): Kodein {
    return Kodein {
        bind<Application>() with instance(app)
        bind<Context>() with instance(app.applicationContext)
        bind<Resources>() with instance(app.applicationContext.resources)
        bind<IgdbService>() with singleton{ IgdbService(instance(), instance()) }
        bind<Moshi>() with singleton {
            Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
        }
        bind<JsonAdapter<CalculatorSerializableState>>() with singleton {
            val moshi: Moshi = instance()
            moshi.adapter<CalculatorSerializableState>(CalculatorSerializableState::class.java)
        }
        bind<JsonAdapter<ExchangeState>>() with singleton {
            val moshi: Moshi = instance()
            moshi.adapter<ExchangeState>(ExchangeState::class.java)
        }
        bind<SharedPreferences>() with singleton {
            app.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }
    }
}