package io.chthonic.igdb.poc.ui.viewholder

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.chthonic.igdb.poc.R
import kotlinx.android.synthetic.main.holder_empty_state.view.*

class EmptyStateHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun createView(parentView: ViewGroup): View {
            return LayoutInflater.from(parentView.context).inflate(R.layout.holder_empty_state, parentView, false)
        }

        fun newInstance(parentView: ViewGroup): EmptyStateHolder {
            return EmptyStateHolder(createView(parentView))
        }
    }

    val iconView: AppCompatImageView by lazy {
       itemView.empty_icon
    }

    val msgView: TextView by lazy {
        itemView.empty_msg
    }

    fun update(isOffline: Boolean, errorMsg: String?) {
        val iconRes = if (isOffline) {
            R.drawable.ic_signal_wifi_off_black_24dp

        } else if (errorMsg.isNullOrEmpty()){
            R.drawable.ic_videogame_asset_grey_24dp

        } else {
            R.drawable.ic_error_black_24dp
        }
        iconView.setImageResource(iconRes)

        val msg = if (!errorMsg.isNullOrEmpty()) {
            errorMsg

        } else {
            "No results. Please try again later"
        }
        msgView.text = msg
    }
}