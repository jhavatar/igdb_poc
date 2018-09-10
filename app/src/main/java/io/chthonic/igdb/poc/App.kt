package io.chthonic.igdb.poc

import android.util.Log
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

        // initialize dependency injection
        kodein = depInject(this)

        // ignore app init if DebugUtils busy analysing
        if (DebugUtils.dontInitSinceAnalsing(this)) {
            return
        }

        // initialize crash reporting
        Fabric.with(this, Crashlytics())

        // initialize logging
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else object: Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                if (priority >= Log.INFO) {
                    Crashlytics.log(message)
                    if (t != null) {
                        Crashlytics.logException(t)
                    }
                }
            }
        })

        // initialize debug tools which assist development process
        DebugUtils.install(this)
    }
}