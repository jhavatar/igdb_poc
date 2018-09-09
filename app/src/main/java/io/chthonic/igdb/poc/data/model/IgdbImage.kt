package io.chthonic.igdb.poc.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jhavatar on 9/7/2018.
 */
@Parcelize
data class IgdbImage(val cloudinary_id: String,
                     val url: String? = null,
                     val width: Int? = null,
                     val height: Int? = null): Parcelable {

    companion object {
        const val WIDTH_LARGE = 227
        const val HEIGHT_LARGE = 320
    }

    val thumbnailUrl: String?
        get() {
            return url?.let {
                 Uri.parse(it)
                        .buildUpon()
                        .scheme("http")
                        .build()
                        .toString()
            } ?: null
        }

    val largeUrl: String?
        get() {
            return url?.let {
                genUrl("cover_big")
            } ?: null
        }

    private fun genUrl(size: String): String {
       return  "http://images.igdb.com/igdb/image/upload/t_$size/$cloudinary_id.jpg"
    }
}