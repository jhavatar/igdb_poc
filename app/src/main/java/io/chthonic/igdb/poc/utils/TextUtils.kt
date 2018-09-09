package io.chthonic.igdb.poc.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jhavatar on 4/2/2018.
 */
object TextUtils {
    internal val dateFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("MMM, yyyy")
    }

    fun getDateString(time: Long): String {
        val date = Date(time)
        return dateFormatter.format(date)
    }

    fun fromHtml(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)

        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml("text")
        }
    }
}