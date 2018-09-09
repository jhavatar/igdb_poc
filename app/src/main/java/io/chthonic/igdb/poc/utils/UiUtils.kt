package io.chthonic.igdb.poc.utils

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import timber.log.Timber


/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    fun tintImageView(imageView: ImageView, activity: Activity, colorRef: Int) {
        DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(activity.getApplicationContext(), colorRef))
    }

    fun isHorizontal(res: Resources): Boolean {
        return res.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun getActivityContentHeight(activity: Activity): Int {
        val contentView = activity.findViewById<View>(android.R.id.content)
        return contentView.height
    }

    fun getMaxHeightForMaxWidth(activity: Activity, imgWidthDip: Int, imgheightDip: Int, maxHeightScreenRatio:Double): Int {
        Timber.d("getMaxHeightForMaxWidth: imgWidthDip = $imgWidthDip, imgheightDip = $imgheightDip")
        val ratio: Float = imgheightDip.toFloat()/Math.max(imgWidthDip.toFloat(), 1.0f) // r = h/w, h = r*w

        val metrics = activity.resources.displayMetrics
        val deviceWidth = metrics.widthPixels
        val deviceHeight = metrics.heightPixels
        val contentHeight = if (activity != null) getActivityContentHeight(activity) else Int.MAX_VALUE
        Timber.d("getMaxHeightForMaxWidth: contentHeight = $contentHeight, deviceHeight = $deviceHeight")
        val maxHeight = (maxHeightScreenRatio * deviceHeight).toInt() //if (fullscreen) Math.min(deviceHeight, contentHeight) else deviceHeight/2 // design decision
        val heightPixels = ratio*deviceWidth

        Timber.d("getMaxHeightForMaxWidth: ratio = $ratio, heightPixels = ${heightPixels.toInt()}, maxHeight = $maxHeight")
        return Math.min(maxHeight, heightPixels.toInt())
    }
}