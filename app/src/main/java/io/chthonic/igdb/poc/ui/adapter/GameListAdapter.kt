package io.chthonic.igdb.poc.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.ui.viewholder.EmptyStateHolder
import io.chthonic.igdb.poc.ui.viewholder.GameHolder
import io.chthonic.igdb.poc.ui.viewholder.TickerHolder
import io.reactivex.subjects.PublishSubject

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameListAdapter(private val gameSelectedPublisher: PublishSubject<Pair<IgdbGame, View>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var hasValidData = false
        private set

    val hasEmptyState: Boolean
    // only display empty state after valid data is set
        get() = hasValidData && gameList.isEmpty()

    var gameList: List<IgdbGame> = listOf()
        set(value) {
            field = value
            errorMessage = null
            hasValidData = true
        }

    var errorMessage: String? = null
        set(value) {
            field = value
            hasValidData = true
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
            holder.update(true, errorMessage)

        } else if (holder is GameHolder) {
            val game = gameList[position]
            holder.update(game)
            RxView.clicks(holder.clickView)
                    .map {
                        Pair(game, holder.imageView)
                    }
                    .subscribeWith(gameSelectedPublisher)

        } else {
            throw RuntimeException("onBindViewHolder: holder $holder is not supported")
        }
    }

}