package io.chthonic.igdb.poc.ui.presenter

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.igdb.poc.App
import io.chthonic.igdb.poc.business.service.IgdbService
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.data.model.Order
import io.chthonic.igdb.poc.ui.model.GameClickResult
import io.chthonic.igdb.poc.ui.vu.MainVu
import io.chthonic.igdb.poc.utils.NetUtils
import io.chthonic.igdb.poc.utils.PagingUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


/**
 * Created by jhavatar on 3/28/2018.
 */
class MainPresenter(kodein: Kodein = App.kodein): BasePresenter<MainVu>() {

    companion object {
        const val NO_PAGE = -1
        const val FIRST_PAGE = 0
        const val LAST_PAGE = 4

        private const val KEY_ORDER = "key_order"
    }

    private val igdbService: IgdbService by kodein.lazy.instance<IgdbService>()

    @Volatile
    private var lastPage = NO_PAGE

    @Volatile
    private var loadingBusy = false

    @Volatile
    private var canLoadMore: Boolean = true

    private var order: Order = Order.POPULARITY

    override fun onLink(vu: MainVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        // read saved state if exists
        if (inState?.containsKey(KEY_ORDER) == true) {
            order = Order.fromId(inState.getInt(KEY_ORDER), order)
        }

        // initialize game page state to match current ui
        loadingBusy = false
        if (vu.getGames().isEmpty()) {
            lastPage = NO_PAGE
            canLoadMore = true
        }

        // initial ui update
        vu.updateOrderSelection(order)
        vu.showAppVersion("v${io.chthonic.igdb.poc.BuildConfig.VERSION_NAME} (${io.chthonic.igdb.poc.BuildConfig.VERSION_CODE})")

        // subscribe ui event listeners
        subscribeVuListeners(vu)

        // auto fetch new games if required
        if (lastPage == NO_PAGE) {
            fetchGames()
        }
    }

    override fun onUnlink() {
        vu?.stopScrollListening()
        vu?.hideLoading(true)
        super.onUnlink()
    }

    override fun onSaveState(outState: Bundle) {
        // persist order state, e.g. to keep across rotation
        outState.putInt(KEY_ORDER, order.id)
        super.onSaveState(outState)
    }

    /**
     * subscribe to (observe) Vu's events until unsubscribed in onUnlink
     */
    private fun subscribeVuListeners(vu: MainVu) {
        rxSubs.add(vu.refreshObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("refreshObservable success")
                    refreshGames(true)

                }, {t: Throwable ->
                    Timber.e(t, "refreshObservable failed")
                }))

        rxSubs.add(vu.startScrollListening(pageSize = IgdbService.PAGE_SIZE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({firstVisibleItemPosition: Int ->
                    Timber.d("setScrollListener: firstVisibleItemPosition = $firstVisibleItemPosition, lastPage = $lastPage, canLoadMore = $canLoadMore, loadingBusy = $loadingBusy")
                    fetchGames()

                }, {t: Throwable ->
                    Timber.e(t, "setScrollListener failed")
                }))

        rxSubs.add(vu.gameSelectedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({event: GameClickResult ->
                    vu.displayGame(event.game, event.image, event.position + 1, order, event.imageView)

                }, {t: Throwable ->
                    Timber.e(t, "gameSelectedObservable failed")
                }))

        rxSubs.add(vu.orderSelectedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({nuOrder: Order ->
                    Timber.d("orderSelectedObservable: nuOrder = $nuOrder, order = $order")
                    if (nuOrder != order) {
                        order = nuOrder
//                        vu?.updateGames(listOf<IgdbGame>())
                        refreshGames(false)
                    }

                }, {t: Throwable ->
                    Timber.e(t, "gameSelectedObservable failed")
                }))
    }


    /**
     * Refresh list by discarding all results and reloading from the first page
     */
    private fun refreshGames(pullToRefresh: Boolean) {
        canLoadMore = true
        lastPage = NO_PAGE
        fetchGames(!pullToRefresh)
    }

    /**
     * Fetch the next page of games
     * @param forceDisplayLoading force if loading should be displayed regardless of logic
     */
    private fun fetchGames(forceDisplayLoading: Boolean? = null) {
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
        if (PagingUtils.shouldShowLoading(forceDisplayLoading, page, FIRST_PAGE, games.isEmpty())) {
            vu?.showLoading()
        }

        val query = when(order) {
            Order.POPULARITY -> igdbService.fetchMostPopularGames(page)
            Order.USER_REVIEW -> igdbService.fetchHighestUserRatedGames(page)
            Order.CRITIC_REVIEW -> igdbService.fetchHighestCriticRatedGames(page)
        }
        rxSubs.add(query
                .flatMap{gameList: List<IgdbGame> ->
                    igdbService.fetchCoverImagesOptimized(gameList).map{ Pair(gameList, it) }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({gamePair ->
                    val (gameList, coverMap) = gamePair
                    Timber.d("fetchGames success: size = ${gameList.size}")
                    onGamesFetchSuccess(gameList, coverMap)
                    loadingBusy = false
                    vu?.hideLoading()

                }, {t: Throwable ->
                    Timber.e(t, "fetchGames failed.")
                    vu?.showError(t.message ?: "fetchGames failed")
                    loadingBusy = false
                    vu?.hideLoading()
                }))
    }

    private fun onGamesFetchSuccess(gameList: List<IgdbGame>, coverMap: Map<Long, IgdbImage>) {
        val page = getNextPage()
        vu?.let {
            lastPage = page
            canLoadMore = PagingUtils.canLoadMorePages(page, FIRST_PAGE, LAST_PAGE, IgdbService.PAGE_SIZE, gameList.size)

            if (page == FIRST_PAGE) {
                it.updateGames(gameList, coverMap)

            } else {
                it.appendGames(gameList, coverMap)
            }
        }
    }

    private fun getNextPage(): Int {
        return Math.max(lastPage + 1, FIRST_PAGE)
    }
}