package io.chthonic.igdb.poc.ui.presenter

import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.igdb.poc.App
import io.chthonic.igdb.poc.business.service.IgdbService
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.Order
import io.chthonic.igdb.poc.ui.vu.MainVu
import io.chthonic.igdb.poc.utils.NetUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.newSingleThreadContext
import timber.log.Timber


/**
 * Created by jhavatar on 3/28/2018.
 */
class MainPresenter(private val kodein: Kodein = App.kodein): BasePresenter<MainVu>() {

    companion object {
        const val NO_PAGE = -1
        const val FIRST_PAGE = 0
        const val LAST_PAGE = 4

        private val KEY_ORDER = "key_order"
    }

    private val igdbService: IgdbService by kodein.lazy.instance<IgdbService>()

    @Volatile
    private var lastPage = NO_PAGE

    @Volatile
    private var loadingBusy = false

    @Volatile
    private var canLoadMore: Boolean = true

    private var order: Order = Order.POPULARITY

    private val clearDispatcher: ThreadPoolDispatcher by lazy {
        newSingleThreadContext("clear")
    }

    override fun onLink(vu: MainVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        // read saved state if exists
        if (inState?.containsKey(KEY_ORDER) == true) {
            order = Order.fromId(inState.getInt(KEY_ORDER), Order.POPULARITY)
        }

        // initialize state and make sure it represents ui
        loadingBusy = false
        if (vu.getGames().isEmpty()) {
            lastPage = NO_PAGE
            canLoadMore = true
        }

        // subscribe event listeners
        subscribeVuListeners(vu)

        // update ui to represent state
        vu.updateOrderSelection(order)

        // auto fetch new games if required
        if (lastPage == NO_PAGE) {
            fetchGames()
        }
    }

    override fun onUnlink() {
        vu?.hideLoading(true)
        super.onUnlink()
    }

    override fun onSaveState(outState: Bundle) {
        outState.putInt(KEY_ORDER, order.id)
        super.onSaveState(outState)
    }

    private fun subscribeVuListeners(vu: MainVu) {
        rxSubs.add(vu.refreshObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("refreshObservable success")
                    refreshGames(true)

                }, {t: Throwable ->
                    Timber.e(t, "refreshObservable failed")
                }))

        rxSubs.add(vu.setScrollListener()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({firstVisibleItemPosition: Int ->
                    Timber.d("setScrollListener: firstVisibleItemPosition = $firstVisibleItemPosition, lastPage = $lastPage, canLoadMore = $canLoadMore, loadingBusy = $loadingBusy")
                    fetchGames()

                }, {t: Throwable ->
                    Timber.e(t, "setScrollListener failed")
                }))

        rxSubs.add(vu.gameSelectedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({event: Pair<IgdbGame, View> ->
                    vu.displayGame(event.first, event.second)

                }, {t: Throwable ->
                    Timber.e(t, "gameSelectedObservable failed")
                }))

        rxSubs.add(vu.orderSelectedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({nuOrder: Order ->
                    Timber.d("orderSelectedObservable: nuOrder = $nuOrder, order = $order")
                    if (nuOrder != order) {
                        order = nuOrder
                        refreshGames(false)
                    }

                }, {t: Throwable ->
                    Timber.e(t, "gameSelectedObservable failed")
                }))
    }


    fun refreshGames(pullToRefresh: Boolean) {
        canLoadMore = true
        lastPage = NO_PAGE
        fetchGames(!pullToRefresh)
    }

    fun fetchGames(forceDisplayLoading: Boolean? = null) {
        if (!canLoadMore || loadingBusy) {
            return
        }

        vu?.let{

            // if offline, do not wait for call to timeout
            if (!NetUtils.isOnline(it.activity)) {
                it.showOffline()
                return
            }
        }

        loadingBusy = true
        val page = getNextPage()
        Timber.d("fetchGames: page = $page, lastPage = $lastPage, loadingBusy = $loadingBusy, canLoadMore = $canLoadMore")

        val games = vu?.getGames() ?: listOf()
        if (forceDisplayLoading ?:
                ((page > 1) || ((page == FIRST_PAGE) && games.isEmpty()))) {
            vu?.showLoading()
        }

        val query = when(order) {
            Order.POPULARITY -> igdbService.fetchMostPopularGames(page)
            Order.USER_REVIEW -> igdbService.fetchHighestUserRatedGames(page)
            Order.CRITIC_REVIEW -> igdbService.fetchHighestCriticRatedGames(page)
        }
        rxSubs.add(query
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({gameList ->
                    Timber.d("fetchGames: size = ${gameList.size}")
                    onGamesFetched(gameList)
                    loadingBusy = false
                    vu?.hideLoading()

                }, {t: Throwable ->
                    Timber.e(t, "fetchGames failed.")
                    vu?.showError(t.message ?: "fetchGames failed")
                    loadingBusy = false
                    vu?.hideLoading()
                }))
    }

    private fun onGamesFetched(gameList: List<IgdbGame>) {
        val page = getNextPage()
        Timber.d("onGamesFetched: size = ${gameList.size}, page = $page")
        vu?.let {

            lastPage = page
            canLoadMore = (page < LAST_PAGE) && ((page >= FIRST_PAGE) && (gameList.size >= IgdbService.PAGE_SIZE))

            if (page == FIRST_PAGE) {
                it.updateGames(gameList)

            } else {
                it.appendGames(gameList)
            }
        }
    }

    private fun getNextPage(): Int {
        return Math.max(lastPage + 1, FIRST_PAGE)
    }
}