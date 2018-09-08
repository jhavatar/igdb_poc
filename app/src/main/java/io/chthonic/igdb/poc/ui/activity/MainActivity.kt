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
import io.chthonic.mythos.mvp.PresenterCacheLoaderCallback
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
        // remove splash theme
        this.setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        // manually tint icon since xml tint does not work
        listOf(Pair(menu.findItem(R.id.action_refresh), R.drawable.ic_refresh_black_24dp),
                Pair(menu.findItem(R.id.action_clear),  R.drawable.ic_delete_black_24dp)).forEach{
            val icon = ResourcesCompat.getDrawable(resources, it.second, theme)
            icon?.setColorFilter(ResourcesCompat.getColor(resources, R.color.white, theme), PorterDuff.Mode.SRC_IN)
            it.first.icon = icon
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Timber.d("onOptionsItemSelected: id = ${item.itemId}, action_refresh = ${R.id.action_refresh}")
        when (item.itemId) {
            R.id.action_refresh -> {
//                mvpDispatcher.presenter?.fetchLatestTickers(true)
                return true
            }
            R.id.action_clear -> {
//                mvpDispatcher.presenter?.clearCalculation()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
