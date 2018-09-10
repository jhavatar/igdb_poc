package io.chthonic.igdb.poc.ui.activity

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by jhavatar on 3/6/17.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val rxSubs : CompositeDisposable = CompositeDisposable()

    override fun onPause() {
        // clear subscriptions to prevent memory leaks
        rxSubs.clear()
        super.onPause()
    }
}