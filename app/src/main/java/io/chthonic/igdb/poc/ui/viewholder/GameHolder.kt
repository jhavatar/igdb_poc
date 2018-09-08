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
import io.chthonic.igdb.poc.utils.TextUtils
import kotlinx.android.synthetic.main.holder_game.view.*

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

    val clickView: View by lazy {
        itemView
    }

    val imageView: ImageView by lazy {
        itemView.game_image
    }

    private val dateView: TextView by lazy {
        itemView.game_date
    }

    fun update(game: IgdbGame) {
        itemView.game_name.text = game.name

        val imageUrl = game.cover?.largeUrl//thumbnailUrl
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .noFade()
                    .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                    .into(imageView)
            imageView.visibility = View.VISIBLE

        } else {
            imageView.setImageResource(0)
            imageView.visibility = View.INVISIBLE
        }

        val position = this.adapterPosition + 1
        itemView.game_value.text = "#$position"

        val date = game.first_release_date
        if (date != null) {
            dateView.text = "Release: ${TextUtils.getDateTimeString(date)}"
            dateView.visibility = View.VISIBLE

        } else {
            dateView.visibility = View.GONE
        }


    }
}