package io.chthonic.igdb.poc.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jhavatar on 9/7/2018.
 */
@Parcelize
data class IgdbImage(val id: Long,
                     val image_id: String,
                     val url: String? = null,
                     val width: Int? = null,
                     val height: Int? = null,
                     val animated: Boolean = false): Parcelable {

    companion object {
        // from https://api-docs.igdb.com/#images
        const val WIDTH_COVER_BIG = 264
        const val HEIGHT_COVER_BIG = 374
    }

    val coverBigUrl: String?
        get() {
            return url?.let {
                genUrl("cover_big")
            }
        }

    private fun genUrl(size: String): String {
       return  "https://images.igdb.com/igdb/image/upload/t_$size/$image_id.jpg"
    }
}