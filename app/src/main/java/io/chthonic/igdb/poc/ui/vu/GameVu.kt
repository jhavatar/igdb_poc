package io.chthonic.igdb.poc.ui.vu

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import io.chthonic.igdb.poc.R

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameVu(inflater: LayoutInflater,
             activity: Activity,
             fragment: Fragment? = null,
             parentView: ViewGroup? = null) : BaseVu(inflater,
        activity = activity,
        fragment = fragment,
        parentView = parentView) {

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_game
    }

//    fun setup(image: )
}