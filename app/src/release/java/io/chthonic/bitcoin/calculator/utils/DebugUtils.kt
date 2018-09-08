package io.chthonic.bitcoin.calculator.utils

import android.app.Application
import okhttp3.OkHttpClient

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
