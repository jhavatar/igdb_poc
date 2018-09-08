package io.chthonic.igdb.poc.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.Currency
import timber.log.Timber


/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    private val mapCurrencyToSign: Map<String, String> by lazy {
        mapOf( Pair(Currency.Bitcoin.code,
                        if (UiUtils.canRenderGlyp("\u20BF")) "\u20BF" else "B"),
                Pair(Currency.Ethereum.code,
                        if (UiUtils.canRenderGlyp("\u039E")) "\u039E" else "E"),
                Pair(Currency.Zar.code, "\u0052"),
                Pair(Currency.Myr.code, "\u0052\u004d"),
                Pair(Currency.Idr.code, "\u0052\u0070"),
                Pair(Currency.Ngn.code,
                        if (UiUtils.canRenderGlyp("\u20a6")) "\u20a6" else "N"))
    }

    fun dipToPixels(dip: Int, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun spToPixels(sp: Int, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun canRenderGlyp(s: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Paint().hasGlyph(s)

        } else {
            EmojiRenderableChecker().isRenderable(s)
        }
    }

    fun getCurrencySign(currency: Currency?, fallback: String? = null): String {
        Timber.d("getCurrencySign: currency = $currency")
        return getCurrencySign(currency?.code, fallback)
    }

    fun getCurrencySign(code: String?, fallback: String? = null): String {
        Timber.d("getCurrencySign: code = $code, fallback = $fallback, mapCurrencyToSign = $mapCurrencyToSign")
        return mapCurrencyToSign[code] ?: (fallback ?: throw RuntimeException("code $code should not exist"))
    }

    fun getCurrencyVectorRes(code: String): Int {
        return when (code) {
            Currency.Bitcoin.code -> R.drawable.ic_xbt
            Currency.Ethereum.code -> R.drawable.ic_eth
            Currency.Zar.code -> R.drawable.ic_zar
            Currency.Myr.code -> R.drawable.ic_myr
            Currency.Idr.code -> R.drawable.ic_idr
            Currency.Ngn.code -> R.drawable.ic_ngn
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    fun getCurrencyImageSmallRes(code: String): Int {
        return when (code) {
            Currency.Bitcoin.code -> R.drawable.ic_xbt_320px
            Currency.Ethereum.code -> R.drawable.ic_eth_320px
            Currency.Zar.code -> R.drawable.ic_zar_320px
            Currency.Myr.code -> R.drawable.ic_myr_320px
            Currency.Idr.code -> R.drawable.ic_idr_320px
            Currency.Ngn.code -> R.drawable.ic_ngn_320px
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    /**
     * note, view requires android:background="?android:attr/selectableItemBackground"
     */
    fun setRipple(view: View) {
        val attrs = intArrayOf(R.attr.selectableItemBackground)
        val typedArray = view.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        view.setBackgroundResource(backgroundResource)
        typedArray.recycle()
    }


    fun getCompoundDrawableForTextDrawable(text: String, tv: TextView, color: Int): TextDrawable {
        val bounds = Rect()
        val textPaint = tv.getPaint()
        textPaint.getTextBounds(text, 0, text.length, bounds)
        val width = bounds.width()

        return TextDrawable.builder()
                .beginConfig()
                .textColor(color)
                .fontSize(tv.textSize.toInt()) // size in px
                .useFont(tv.typeface)
                .width(width) // size in px
                .endConfig()
                .buildRect(text, Color.TRANSPARENT)
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