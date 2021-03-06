package io.chthonic.igdb.poc.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.ui.model.GameClickResult
import io.chthonic.igdb.poc.ui.viewholder.EmptyStateHolder
import io.chthonic.igdb.poc.ui.viewholder.GameHolder
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameListAdapter(private val gameSelectedPublisher: PublishSubject<GameClickResult>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_GAME = 1
    }

    // Is true if a value has been explicitly set, be it data or error
    var hasValidData = false
        private set

    val hasEmptyState: Boolean
        get() = hasValidData && gameList.isEmpty() // only display empty state after valid data is set

    var gameList: List<IgdbGame> = listOf()
        set(value) {
            field = value
            errorMessage = null
            hasValidData = true
        }

    val coverMap: MutableMap<Long, IgdbImage> = mutableMapOf()

    var errorMessage: String? = null
        set(value) {
            field = value
            hasValidData = true
        }

    override fun getItemViewType(position: Int): Int {
        return if (hasEmptyState) {
            TYPE_EMPTY

        } else {
            TYPE_GAME
        }
    }

    override fun getItemCount(): Int {
        return if (hasEmptyState) {
            1

        } else {
            gameList.size
        }
    }

    override fun onCreateViewHolder(parentView: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (hasEmptyState) {
            EmptyStateHolder.newInstance(parentView)

        } else {
            GameHolder.newInstance(parentView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EmptyStateHolder) {
            holder.update(errorMessage == holder.itemView.context.getString(R.string.error_no_internet), errorMessage)

        } else if (holder is GameHolder) {
            val game = gameList[position]
            val image = coverMap.get(game.cover)
            //holder.update(game, if (game.cover != null) igdbService.fetchCoverImages(listOf(game.cover)) else null)
            holder.update(game, image)
            RxView.clicks(holder.clickView)
                    .map {
                        GameClickResult(game, position, image, WeakReference<View>(holder.imageView))
                    }
                    .subscribeWith(gameSelectedPublisher)

        } else {
            throw RuntimeException("onBindViewHolder: holder $holder is not supported")
        }
    }

}