package io.chthonic.igdb.poc.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.utils.TextUtils
import io.chthonic.igdb.poc.utils.UiUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.holder_game.view.*
import timber.log.Timber

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun createView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context).inflate(R.layout.holder_game, parent, false)
        }

        fun newInstance(parentView: ViewGroup): GameHolder {
            return GameHolder(GameHolder.createView(parentView))
        }
    }

    private var gameId: Long? = null
    private var sub : Disposable? = null
    private var busyFetchingImage: Boolean = false

    val clickView: View by lazy {
        itemView
    }

    val imageView: ImageView by lazy {
        itemView.game_image
    }

    private val dateView: TextView by lazy {
        itemView.game_date
    }

    fun update(game: IgdbGame, coverImage: IgdbImage?) {//coverRequest: Single<List<IgdbImage>>?) {
        itemView.game_name.text = game.name

        if (coverImage != null) {
            Picasso.get()
                    .load(coverImage.largeUrl)
                    .noFade()
                    .placeholder(R.drawable.ic_videogame_asset_grey_24dp)
                    .into(imageView)
            imageView.visibility = View.VISIBLE

        } else {
            imageView.visibility = View.INVISIBLE
        }

//        val change = gameId != game.id
//        val imageIncomplete = !busyFetchingImage && (imageView.visibility != View.VISIBLE)
//        if (change || imageIncomplete) {
//            gameId = game.id
//            sub?.dispose()
//            sub = null
//
//            val img = game.cover?.let {
//                UiUtils.coverImageCache.get(it)
//            }
//            if (img != null) {
//                showImage(img)
//
//            } else if (coverRequest != null) {
//                Timber.d("coverCache success ${game.cover}")
//                imageView.setImageResource(R.drawable.ic_videogame_asset_grey_24dp)
//                imageView.visibility = View.VISIBLE
//
//                busyFetchingImage = true
//                sub = coverRequest
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ imgList ->
//                            busyFetchingImage = false
//                            val img = imgList.firstOrNull()
//                            Timber.d("coverRequest success: imgList = $imgList")
//                            if (img != null) {
//                                showImage(img)
//
//                            } else {
//                                hideImage()
//                            }
//
//                        }, { t: Throwable ->
//                            busyFetchingImage = false
//                            Timber.e(t, "coverRequest failed")
//                        })
//
//            } else {
//                hideImage()
//            }
//        }

        val position = this.adapterPosition + 1
        itemView.game_value.text = "#$position"

        val date = game.first_release_date
        if (date != null) {
            dateView.text = "Release: ${TextUtils.getDateString(date)}"
            dateView.visibility = View.VISIBLE

        } else {
            dateView.visibility = View.GONE
        }
    }


    private fun showImage(img: IgdbImage) {
        Timber.d("showImage: largeUrl = ${img.largeUrl}")
        UiUtils.coverImageCache.put(img.id, img)
        Picasso.get()
                .load(img.largeUrl)
                .noFade()
                .placeholder(R.drawable.ic_videogame_asset_grey_24dp)
                .into(imageView)
        imageView.visibility = View.VISIBLE
    }

    private fun hideImage() {
        imageView.setImageResource(0)
        imageView.visibility = View.INVISIBLE
    }
}