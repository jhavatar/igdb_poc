package io.chthonic.igdb.poc

import com.crashlytics.android.Crashlytics
import com.github.salomonbrys.kodein.Kodein
import io.chthonic.igdb.poc.utils.DebugUtils
import io.fabric.sdk.android.Fabric
import timber.log.Timber



/**
 * Created by jhavatar on 3/2/17.
 */
class App : android.app.Application() {
    companion object {
        lateinit var kodein: Kodein
            private set
    }

    override fun onCreate() {
        super.onCreate()

        kodein = depInject(this)

        if (DebugUtils.dontInitSinceAnalsing(this)) {
            return
        }
        Fabric.with(this, Crashlytics())
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else object: Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                // Nothing
            }
        })
        DebugUtils.install(this)
    }
}