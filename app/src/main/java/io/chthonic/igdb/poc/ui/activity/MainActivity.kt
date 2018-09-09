package io.chthonic.igdb.poc.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.ui.presenter.MainPresenter
import io.chthonic.igdb.poc.ui.vu.MainVu
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.PresenterCacheBasicLazy
import io.chthonic.mythos.viewmodel.PesenterCacheViewModel
import timber.log.Timber


class MainActivity : MVPActivity<MainPresenter, MainVu>() {

    companion object {
        private val MVP_UID by lazy {
            MainActivity::class.hashCode()
        }
    }

    override fun createMVPDispatcher(): MVPDispatcher<MainPresenter, MainVu> {
        @Suppress("UNCHECKED_CAST")
        val viewModel = ViewModelProviders.of(this).get(MVP_UID.toString(), PesenterCacheViewModel::class.java)
                as PesenterCacheViewModel<MainPresenter>

        // store/retrieve MVP's presenter on ViewModel (unfortunate name) object: ViewModel is lifecycle aware and has features like surviving rotation
        val presenterCache = viewModel.cache ?: run {
            val cache = PresenterCacheBasicLazy({ MainPresenter() }, false)
            viewModel.cache = cache
            cache
        }
        return MVPDispatcher(MVP_UID,
                presenterCache,
                ::MainVu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // NB, remove splash theme before super.onCreate
        this.setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }
}
