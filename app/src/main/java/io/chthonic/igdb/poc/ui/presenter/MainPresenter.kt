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
import io.chthonic.igdb.poc.utils.UiUtils
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

        // force update all ui
//        vu.updateCalculation(genCalculationViewModel(calculatorService.state).copy(forceSet = true))
//        vu.updateTickers(genTickerViewModels(igdbService.state.tickers))
        Timber.d("ui init completed")

        loadingBusy = false
        subscribeServiceListeners()
        subscribeVuListeners(vu)
        Timber.d("listeners subscribe completed")

        vu.updateOrderSelection(order)

        if (lastPage == NO_PAGE) {
            fetchGames()
        }
    }

    override fun onUnlink() {
        vu?.hideLoading(true)
        super.onUnlink()
    }

    private fun subscribeServiceListeners() {

//        rxSubs.add(igdbService.observers.tickersChangeObserver
//                .subscribeOn(Schedulers.computation())
//                .map {tickerMap: Map<String, Ticker> ->
//                    genTickerViewModels(tickerMap)
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({tickers: List<TickerViewModel> ->
//                    Timber.d("tickersChangeObserver success = $tickers")
//                    vu?.updateTickers(tickers)
//
//                }, {it: Throwable ->
//                    Timber.e(it, "tickersChangeObserver fail")
//                }))
//
//        rxSubs.add(calculatorService.observers.calculationChangeChangeObserver
//                .subscribeOn(Schedulers.computation())
//                .map{ calcState: CalculatorState ->
//                    val exchangeState = igdbService.state
//                    Pair<CalculationViewModel, List<TickerViewModel>>(genCalculationViewModel(calcState, exchangeState),
//                            genTickerViewModels(exchangeState.tickers, calcState))
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({viewModels: Pair<CalculationViewModel, List<TickerViewModel>> ->
//                    Timber.d("calculationChangeChangeObserver success = $viewModels")
//                    vu?.updateCalculation(viewModels.first)
//                    vu?.updateTickers(viewModels.second)
//
//                }, {it: Throwable ->
//                    Timber.e(it, "fetchTickerLot fail")
//                }))
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

        rxSubs.add(vu.setScrollListener() // articles per page not guaranteed
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

//        rxSubs.add(vu!!.tickerSelectObservable
//                .observeOn(Schedulers.computation())
//                .subscribe({ tickerCode: String ->
//                    Timber.d("tickerSelectObservable success: $tickerCode")
//                    calculatorService.setTargetTicker(tickerCode)
//
//                }, {t: Throwable ->
//                    Timber.e(t, "tickerSelectObservable failed")
//                }))
//
//        rxSubs.add(vu!!.leftInputObserver
//                .observeOn(Schedulers.computation())
//                .subscribe({amount: String ->
//                    Timber.d("leftInputObserver success = $amount")
//                    calculatorService.updateSourceDirectionAndSourceValue(true, amount)
//
//                }, {
//
//                    Timber.e(it, "leftInputObserver failed")
//                }))
//
//        rxSubs.add(vu!!.rightInputObserver
//                .observeOn(Schedulers.computation())
//                .subscribe({amount: String ->
//                    Timber.d("rightInputObserver success = $amount")
//                    calculatorService.updateSourceDirectionAndSourceValue(false, amount)
//
//                }, {
//
//                    Timber.e(it, "rightInputObserver failed")
//                }))
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
//    fun fetchLatestTickers(forceDisplayLoading: Boolean = false) {
//        Timber.d("fetchLatestTickers: forceDisplayLoading = $forceDisplayLoading")
//        if (forceDisplayLoading || igdbService.state.tickers.isEmpty()) {
//            vu?.showLoading()
//        }
//
//        rxSubs.add(igdbService.fetchTickerLot()
////                .toCompletable()
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({it: List<Ticker> ->
//                    Timber.d("fetchTickerLot success = $it")
//                    vu?.hideLoading()
//
//                }, {it: Throwable ->
//                    Timber.e(it, "fetchTickerLot fail")
//                    vu?.hideLoading()
//                }))
//    }

//    fun clearCalculation() {
////        launch(clearDispatcher) {
//////            Timber.d("clearCalculation: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}")
////            calculatorService.clear()
////        }
//    }


//    private fun genCalculationViewModel(calculatorState: CalculatorState, exchangeState: ExchangeState = igdbService.state): CalculationViewModel {
//        Timber.d("genCalculationViewModel: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, calculatorState = $calculatorState")
//        val leftTicker = CalculatorUtils.getLeftTicker(calculatorState, exchangeState)
//        val leftTickerViewModel = if (leftTicker != null) {
//            val leftCurrency = ExchangeUtils.getCurrencyForTicker(leftTicker)
//            TickerViewModel(leftTicker.code, leftTicker.code,
//                    TextUtils.formatCurrency(CalculatorUtils.getPrice(leftTicker, calculatorState, exchangeState), leftCurrency?.decimalDigits),
//                    UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(leftTicker), ""),
//                    leftCurrency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
//                    true,
//                    false,
//                    calculatorState.leftTickerIsSource,
//                    TextUtils.getDateTimeString(leftTicker.timestamp))
//        } else {
//            null
//        }
//        Timber.d("genCalculationViewModel: leftTicker = $leftTicker, \nleftTickerViewModel = $leftTickerViewModel")
//
//        val rightTicker = CalculatorUtils.getRightTicker(calculatorState, exchangeState)
//        val rightTickerViewModel = if (rightTicker != null) {
//            val rightCurrency = ExchangeUtils.getCurrencyForTicker(rightTicker)
//            TickerViewModel(rightTicker.code, rightTicker.code,
//                    TextUtils.formatCurrency(CalculatorUtils.getPrice(rightTicker, calculatorState, exchangeState), rightCurrency?.decimalDigits),
//                    UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(rightTicker), ""),
//                    rightCurrency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
//                    false,
//                    true,
//                    !calculatorState.leftTickerIsSource,
//                    TextUtils.getDateTimeString(rightTicker.timestamp))
//        } else {
//            null
//        }
//        Timber.d("genCalculationViewModel: rightTicker = $rightTicker, \nrightTickerViewModel = $rightTickerViewModel")
//
////        Timber.d("genCalculationViewModel: currency = ${ExchangeUtils.getCurrencyForTicker(ticker!!)}")
//        return CalculationViewModel(
//                calculatorState.leftTickerIsSource,
//                leftTicker = leftTickerViewModel,
//                rightTicker = rightTickerViewModel,
//                forceSet = (CalculatorState.getFactoryState() == calculatorState))
//    }
//
//    private fun genTickerViewModels(tickers: Map<String, Ticker>, calcState: CalculatorState = calculatorService.state): List<TickerViewModel> {
//        Timber.d("genTickerViewModels: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, tickers = $tickers")
//        val leftTicker = CalculatorUtils.getLeftTicker(calcState, tickers)
//        val rightTicker = CalculatorUtils.getRightTicker(calcState, tickers)
//        return tickers.values
//                .filter{ ExchangeUtils.isSupportedCurrency(it) }
//                .sortedBy { it.code }
//                .map {
//                    val currency = ExchangeUtils.getCurrencyForTicker(it)
//                    val isLeft = leftTicker?.code == it.code
//                    val isRight = rightTicker?.code == it.code
//                    val isSource = (isLeft && calcState.leftTickerIsSource) || (isRight && !calcState.leftTickerIsSource)
//                    TickerViewModel(it.code,
//                            it.code,
//                            TextUtils.formatCurrency(CalculatorUtils.getPrice(it, calculatorService.state, tickers), currency?.decimalDigits),
//                            UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(it), ""),
//                            currency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
//                            isLeft,
//                            isRight,
//                            isSource,
//                            TextUtils.getDateTimeString(it.timestamp))
//                }
//    }

}