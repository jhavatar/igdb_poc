package io.chthonic.igdb.poc.ui.viewholder

import android.content.res.ColorStateList
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.chthonic.igdb.poc.R
import kotlinx.android.synthetic.main.holder_empty_state.view.*

class EmptyStateHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

//    ,
//    private val emptyTitle: String,
//    private val emptyMsg: String,
//    private val iconRes: Int?,
//    private val emptyStateNoInternetMsg: String?,
//    private val fullScreen: Boolean

    companion object {
        fun createView(parentView: ViewGroup): View {
            return LayoutInflater.from(parentView.context).inflate(R.layout.holder_empty_state, parentView, false)
        }

        fun newInstance(parentView: ViewGroup): EmptyStateHolder {
            return EmptyStateHolder(createView(parentView))
        }

//        fun newInstance(parentView: ViewGroup, emptyTitle: String, emptyMsg: String, iconRes: Int?, emptyStateNoInternetMsg: String? ,fullScreen: Boolean): EmptyStateHolder {
//            return EmptyStateHolder(createView(parentView), emptyTitle, emptyMsg, iconRes, emptyStateNoInternetMsg,fullScreen)
//        }
    }

    val titleView: TextView by lazy {
        itemView.empty_title
    }

    val iconView: AppCompatImageView by lazy {
       itemView.empty_icon
    }

    val msgView: TextView by lazy {
        itemView.empty_msg
    }

//    init {
//        val container = itemView.empty_container
//        if (!fullScreen) {
//            container.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//
//        } else {
//            container.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//        }
//    }

    fun update(isOnline: Boolean, errorMsg: String?) {
//        val iconRes = if (!isOnline) {
//            R.drawable.ic_signal_wifi_off_black_24dp
//
//        } else {
//            iconRes
//        }
//        if (iconRes != null) {
//            iconView.setImageResource(iconRes)
//            iconView.visibility = View.VISIBLE
//        } else {
//            iconView.visibility = View.GONE
//        }
//
//        val title = if (!isOnline) {
//            itemView.resources.getString(R.string.empty_state_no_internet_title)
//
//        } else {
//            emptyTitle
//        }
//        titleView.text = title
//
//
//        val msg = if (!isOnline) {
//            if (emptyStateNoInternetMsg != null) {
//                emptyStateNoInternetMsg
//            } else {
//                itemView.resources.getString(R.string.empty_state_no_internet_msg)
//            }
//
//        } else if (!errorMsg.isNullOrEmpty()) {
//            errorMsg
//
//        } else {
//            emptyMsg
//        }
//        msgView.text = msg
    }
}