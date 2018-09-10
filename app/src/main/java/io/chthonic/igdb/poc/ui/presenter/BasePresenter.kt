package io.chthonic.igdb.poc.ui.presenter

/**
 * Created by jhavatar on 3/7/17.
 */
abstract class BasePresenter<V>: io.chthonic.mythos.mvp.Presenter<V>() where V : io.chthonic.mythos.mvp.Vu {

    protected val rxSubs : io.reactivex.disposables.CompositeDisposable by lazy {
        io.reactivex.disposables.CompositeDisposable()
    }

    override fun onUnlink() {
        // clear subscriptions to prevent memory leaks
        rxSubs.clear()
        super.onUnlink()
    }

}