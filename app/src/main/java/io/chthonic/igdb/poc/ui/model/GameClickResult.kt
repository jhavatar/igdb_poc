package io.chthonic.igdb.poc.ui.model

import android.view.View
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import java.lang.ref.WeakReference

/**
 * Created by jhavatar on 1/9/2019.
 */
data class GameClickResult(val game: IgdbGame, val position: Int, val image: IgdbImage?, val imageView: WeakReference<View>) {
}