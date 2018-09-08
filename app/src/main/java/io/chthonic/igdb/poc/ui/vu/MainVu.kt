package io.chthonic.igdb.poc.ui.vu

import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.app.Activity
import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.ui.activity.GameActivity
import io.chthonic.igdb.poc.ui.adapter.GameListAdapter
import io.chthonic.igdb.poc.ui.presenter.GamePresenter
import io.chthonic.igdb.poc.ui.view.InfiniteLinearScrollListener
import io.chthonic.igdb.poc.utils.UiUtils
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.vu_main.view.*
import timber.log.Timber
import android.support.v4.app.ActivityOptionsCompat
import android.widget.ImageView


/**
 * Created by jhavatar on 3/28/2018.
 */
class MainVu(inflater: LayoutInflater,
             activity: Activity,
             fragment: Fragment? = null,
             parentView: ViewGroup? = null) : BaseVu(inflater,
        activity = activity,
        fragment = fragment,
        parentView = parentView) {


    private val refreshPublisher: PublishSubject<Any> by lazy {
        PublishSubject.create<Any>()
    }

    val refreshObservable: Observable<Any>
        get() = refreshPublisher.hide()

    private val scrollEndPublisher: PublishSubject<Int> by lazy {
        PublishSubject.create<Int>()
    }

    private val gameSelectedPublisher: PublishSubject<Pair<IgdbGame, View>> by lazy {
        PublishSubject.create<Pair<IgdbGame, View>>()
    }
    val gameSelectedObservable: Observable<Pair<IgdbGame, View>>
        get() = gameSelectedPublisher.hide()

    private val listView: RecyclerView by lazy {
        rootView.list_tickers
    }

    private val listLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }

    private val appBarOnChangeListener: AppBarLayout.OnOffsetChangedListener by lazy {
        object: AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, offset: Int) {
                appBarExpanded = offset == 0
            }
        }
    }
    private val adapter: GameListAdapter by lazy {
        GameListAdapter(gameSelectedPublisher)
    }

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_main
    }

    private var appBarExpanded: Boolean = true

    fun getGames(): List<IgdbGame> {
        return adapter.gameList
    }

    fun setScrollListener(pageSize: Int? = null, preemptThresholdCount: Int = 0): Observable<Int> {
        listView.clearOnScrollListeners()
        listView.addOnScrollListener(
                object: InfiniteLinearScrollListener(listLayoutManager, pageSize, preemptThresholdCount) {
                    override fun onScrolledToEnd(firstVisibleItemPosition: Int) {
                        scrollEndPublisher.onNext(firstVisibleItemPosition)
                    }
                })
        return scrollEndPublisher.hide()
    }

    override fun onCreate() {
        super.onCreate()

        (activity as AppCompatActivity).setSupportActionBar(rootView.toolbar)

        rootView.app_bar.addOnOffsetChangedListener(appBarOnChangeListener)
        rootView.pullToRefresh.headerView = View.inflate(activity, R.layout.layout_loading, null)

        rootView.pullToRefresh.setPtrHandler(object: PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                Timber.d("onRefreshBegin")
                refreshPublisher.onNext(true)
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return (listLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) && appBarExpanded
            }

        })

        listView.adapter = adapter
        listView.layoutManager = listLayoutManager
        listView.addItemDecoration(DividerItemDecoration(listView.getContext(), listLayoutManager.getOrientation()))
        val anim = SlideInUpAnimator(OvershootInterpolator(2.0f))
        listView.itemAnimator = anim //SlideInUpAnimator(OvershootInterpolator(2.0f))

        if (UiUtils.isHorizontal(rootView.resources)) {
            rootView.app_bar.setExpanded(false)
        }
    }

    override fun onDestroy() {
        // clear mem
        listView.clearOnScrollListeners()
        rootView.pullToRefresh.setPtrHandler(null)
        rootView.app_bar.removeOnOffsetChangedListener(appBarOnChangeListener)
        super.onDestroy()
    }


//    private fun updateActivated(leftTickerIsSource: Boolean): Boolean {
//        val activatedChange = (leftInfoLayout.isActivated != leftTickerIsSource) || (rightInfoLayout.isActivated != !leftTickerIsSource)
//        if (!activatedChange) {
//            return false
//        }
//
//        leftInfoLayout.isActivated = leftTickerIsSource
//        rootView.layout_left_input.isActivated = leftTickerIsSource
//        rightInfoLayout.isActivated = !leftTickerIsSource
//        rootView.layout_right_input.isActivated = !leftTickerIsSource
//
//        leftInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                UiUtils.getCompoundDrawableForTextDrawable(
//                        UiUtils.getCurrencySign(Currency.Bitcoin),
//                        leftInput,
//                        if (leftTickerIsSource) leftInput.resources.getColor(R.color.secondaryColor) else leftInput.currentTextColor),
//                null,null, null)
//
//        return true
//    }
//
//
//    fun updateCalculation(calc: CalculationViewModel) {
//        Timber.d("updateCalculation: calc = $calc")
//        if (leftInput.isFocused != calc.leftTickerIsSource) {
//            leftInput.requestFocus()
//        }
//        if (rightInput.isFocused != !calc.leftTickerIsSource) {
//            rightInput.requestFocus()
//        }
//
//        val convertDirectChanged = updateActivated(calc.leftTickerIsSource)
//
//        if (calc.forceSet || calc.leftTickerIsSource) {
//            rightInput.removeTextChangedListener(rightInputWatcher)
//            val text = calc.rightTicker?.price ?: TextUtils.PLACE_HOLDER_STRING
//            val tooManyDigits = (text.length > rightInputLength)
//
//            if (tooManyDigits) {
//                rightInput.setText(TextUtils.TOO_MANY_DIGITS_MSG)
//
//            } else {
//                rightInput.setText(text)
//            }
//
//            if (text == TextUtils.PLACE_HOLDER_STRING) {
//                rightInput.isEnabled = false
//
//            } else {
//
//                // must be able to change text if selected
//                if (!tooManyDigits || !calc.leftTickerIsSource) {
//                    rightInput.addTextChangedListener(rightInputWatcher)
//                    rightInput.isEnabled = true
//
//                } else {
//                    rightInput.isEnabled = false
//                }
//            }
//        }
//
//        if (calc.forceSet || !calc.leftTickerIsSource) {
//            leftInput.removeTextChangedListener(leftInputWatcher)
//            val text = calc.leftTicker?.price ?: TextUtils.PLACE_HOLDER_STRING
////            Timber.d("setBitcoin: text = $text, length = ${text.length}, maxLength = ${leftInputLength}")
//            if (text.length > leftInputLength) {
//                leftInput.setText(TextUtils.TOO_MANY_DIGITS_MSG)
//                if (calc.leftTickerIsSource) {
//                    // must be able to change text if selected
//                    leftInput.addTextChangedListener(leftInputWatcher)
//
//                } else {
//                    leftInput.isEnabled = false
//                }
//
//            } else {
//                leftInput.isEnabled = true
//                leftInput.setText(text)
//                leftInput.addTextChangedListener(leftInputWatcher)
//            }
//        }
//
//        val nuLeftName = calc.leftTicker?.name ?: TextUtils.PLACE_HOLDER_STRING
//        val leftNameChanged = leftName.text != nuLeftName
//        // update left image and label
//        if (leftNameChanged) {
//            leftName.text = nuLeftName
//            if (calc.leftTicker != null) {
//                leftImage.setImageResource(UiUtils.getCurrencyVectorRes(calc.leftTicker.code))
//
//            } else {
//                leftImage.setImageDrawable(null)
//            }
//        }
//
//        // update right compound image
//        if (leftNameChanged || convertDirectChanged)  {
//            if (calc.leftTicker != null) {
//                leftInputWatcher.updateInputType(calc.leftTicker.decimalDigits)
//                leftInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        UiUtils.getCompoundDrawableForTextDrawable(
//                                UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(calc.leftTicker.code)),
//                                leftInput,
//                                if (calc.leftTickerIsSource) leftInput.resources.getColor(R.color.secondaryColor) else leftInput.currentTextColor),
//                        null, null, null)
//
//            } else {
//                leftInput.setCompoundDrawablesRelative(null, null, null, null)
//            }
//        }
//
//
//        val nuRightName = calc.rightTicker?.name ?: TextUtils.PLACE_HOLDER_STRING
//        val rightNameChanged = rightName.text != nuRightName
//
//        // update right image and label
//        if (rightNameChanged) {
//            rightName.text = nuRightName
//            if (calc.rightTicker != null) {
//                rightImage.setImageResource(UiUtils.getCurrencyVectorRes(calc.rightTicker.code))
//
//            } else {
//                rightImage.setImageDrawable(null)
//            }
//        }
//
//        // update right compound image
//        if (rightNameChanged || convertDirectChanged)  {
//            if (calc.rightTicker != null) {
//                rightInputWatcher.updateInputType(calc.rightTicker.decimalDigits)
//                rightInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        UiUtils.getCompoundDrawableForTextDrawable(
//                                UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(calc.rightTicker.code)),
//                                rightInput,
//                                if (!calc.leftTickerIsSource) rightInput.resources.getColor(R.color.secondaryColor) else rightInput.currentTextColor),
//                        null, null, null)
//
//            } else {
//                rightInput.setCompoundDrawablesRelative(null, null, null, null)
//            }
//        }
//    }


    override fun hideLoading() {
        rootView.pullToRefresh.refreshComplete()
        super.hideLoading()
    }


    fun appendGames(nuGames: List<IgdbGame>) {
        val hadEmptyState = adapter.hasEmptyState
        val games = adapter.gameList.toMutableList()
        val appendStart = games.size
        games.addAll(nuGames)
        adapter.gameList = games

        if (hadEmptyState){
            if (nuGames.size >= 1) {
                adapter.notifyItemRangeChanged(0,  1)
            }

            if (nuGames.size > 1) {
                adapter.notifyItemRangeInserted(1,  nuGames.size - 1)
            }

        } else {
            adapter.notifyItemRangeInserted(appendStart, nuGames.size)
        }
    }

    fun updateGames(nuGames: List<IgdbGame>) {
        val hadEmptyState = adapter.hasEmptyState
        val oldSize = adapter.gameList.size
        val newSize = nuGames.size

        if (!hadEmptyState && (oldSize == newSize)) {
            return
        }
        adapter.gameList = nuGames


        if ((oldSize == 0) && (newSize == 0)) {
            adapter.notifyItemRangeChanged(0, adapter.itemCount)

        } else if (oldSize == 0) {
            if (hadEmptyState) {
                adapter.notifyItemRangeChanged(0, 1)
                adapter.notifyItemRangeInserted(1, newSize - 1)

            } else {
                adapter.notifyItemRangeInserted(0, newSize)
            }

        } else if (newSize == 0) {
            if (hadEmptyState) {
                if (oldSize > 1) {
                    adapter.notifyItemRangeRemoved(1, oldSize - 1)
                }
                adapter.notifyItemRangeChanged(0, 1)

            } else {
                adapter.notifyItemRangeRemoved(0, oldSize)
            }

        } else if (oldSize == newSize) {
            adapter.notifyItemRangeChanged(0, newSize)

        } else if (oldSize < newSize) {
            adapter.notifyItemRangeChanged(0, oldSize)
            adapter.notifyItemRangeInserted(oldSize, newSize - oldSize)

        } else if (oldSize > newSize) {
            adapter.notifyItemRangeChanged(0, newSize)
            adapter.notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }


    fun displayGame(game: IgdbGame, sharedView: View) {
        val intent = Intent(activity, GameActivity::class.java)
        intent.putExtra(GamePresenter.KEY_GAME, game)
        if (game.cover?.largeUrl != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView as View, "cover")
            activity.startActivity(intent, options.toBundle())

        } else {
            activity.startActivity(intent)
        }

    }
}