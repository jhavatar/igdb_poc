package io.chthonic.igdb.poc.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jhavatar on 9/7/2018.
 */
@Parcelize
data class IgdbGame(val id: Long,
                    val slug: String,
                    val name: String,
                    val summary: String? = null,
                    val popularity: Double? = null,
                    val total_rating: Double? = null,
                    val first_release_date: Long? = null,
                    val cover: IgdbImage? = null): Parcelable