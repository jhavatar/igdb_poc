package io.chthonic.igdb.poc.ui.viewholder

import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.ui.model.TickerViewModel
import io.chthonic.igdb.poc.utils.UiUtils
import kotlinx.android.synthetic.main.holder_ticker.view.*
import timber.log.Timber

/**
 * Created by jhavatar on 3/28/2018.
 */
class TickerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun createView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context).inflate(R.layout.holder_ticker, parent, false)
        }
    }

    fun update(ticker: TickerViewModel) {

        val isEnabled = !ticker.isSource
//        var x: ViewGroup = itemView as ViewGroup
//        for (i in 0 until x.getChildCount()) {
//            val child = x.getChildAt(i)
//            child.setEnabled(isEnabled)
//        }
        if (ticker.isLeft) {
            val color = if (ticker.isSource) R.color.primaryDarkColor else R.color.primaryLightColor
//            val drawable = if (ticker.isSource) R.drawable.bg_round_left_source else R.drawable.bg_round_left_sink
            itemView.ticker_left.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, color, itemView.context.theme))
//            Timber.d("left drawable = $drawable")
//            itemView.ticker_left.setBackgroundResource(drawable)// = itemView.resources.getDrawable(drawable) //ResourcesCompat.getDrawable(itemView.resources, drawable, itemView.context.theme)
            itemView.ticker_left.visibility = View.VISIBLE
//            itemView.ticker_left.refreshDrawableState()

        } else {
            itemView.ticker_left.visibility = View.INVISIBLE
        }

        if (ticker.isRight) {
            val color = if (ticker.isSource) R.color.primaryDarkColor else R.color.primaryLightColor
//            val drawable = if (ticker.isSource) R.drawable.bg_round_right_source else R.drawable.bg_round_right_sink
//            Timber.d("right drawable = $drawable")
            itemView.ticker_right.setBackgroundColor(ResourcesCompat.getColor(itemView.resources, color, itemView.context.theme))
//            itemView.ticker_right.setBackgroundResource(drawable)// = ResourcesCompat.getDrawable(itemView.resources, drawable, itemView.context.theme)
            itemView.ticker_right.visibility = View.VISIBLE

        } else {
            itemView.ticker_right.visibility = View.INVISIBLE
        }
//        itemView.ticker_left.visibility = if (ticker.isLeft) View.VISIBLE else View.GONE
//        itemView.ticker_right.visibility = if (ticker.isRight) View.VISIBLE else View.GONE
//        itemView.isEnabled = isEnabled


        itemView.ticker_name.text = ticker.name

        val price = "<b>${ticker.sign}</b> ${ticker.price}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemView.ticker_price.text = Html.fromHtml(price,  Html.FROM_HTML_MODE_COMPACT)
        } else {
            itemView.ticker_price.text = Html.fromHtml(price)
        }

        itemView.ticker_date.text = itemView.resources.getString(R.string.updated) + " ${ticker.dateTime}"

        // note, circleImageView does not work with vectors
        itemView.ticker_image.setImageResource(UiUtils.getCurrencyImageSmallRes(ticker.code))

        Timber.d("update: code = ${ticker.code}, isLeft = ${ticker.isLeft}, isRight = ${ticker.isRight}, isSource = ${ticker.isSource}, isSink = ${ticker.isSink}")
        val selectedVis = if (ticker.isSink) View.VISIBLE else View.GONE //if (ticker.isRight) View.VISIBLE else View.GONE
//        if (itemView.ticker_selected.visibility != selectedVis) {
//            itemView.ticker_selected.visibility = selectedVis
            itemView.ticker_image.borderColor = ResourcesCompat.getColor(itemView.resources, if (ticker.isSource) {
                R.color.secondaryColor

            } else if (ticker.isSink) {
                R.color.primaryLightColor

            } else {
                R.color.primaryDarkestColor
            }, itemView.context.theme)
            itemView.ticker_image.borderWidth = if (ticker.isSink || ticker.isSource) UiUtils.dipToPixels(4, itemView.context) else UiUtils.dipToPixels(1, itemView.context)
//        }


    }

}