package io.chthonic.igdb.poc.utils

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created by jhavatar on 9/12/16.
 */
object DebugUtils {

    lateinit private var refwatcher: RefWatcher

    fun install(app: Application) {
        refwatcher = LeakCanary.install(app)
    }

    fun dontInitSinceAnalsing(app: Application): Boolean {
        return LeakCanary.isInAnalyzerProcess(app)
    }

    fun watchForLeaks(watchedReference: Any) {
        refwatcher.watch(watchedReference)
    }
}
