package io.chthonic.igdb.poc.utils

import android.app.Application

/**
 * Created by jhavatar on 9/12/16.
 */
object DebugUtils {

    fun install(app: Application) {
        // do nothing
    }

    fun dontInitSinceAnalsing(app: Application): Boolean {
        return false
    }

    fun watchForLeaks(watchedReference: Any) {
        // do nothing
    }
}
