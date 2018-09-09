package io.chthonic.igdb.poc.ui.vu

import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.app.Activity
import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
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
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearSmoothScroller
import android.view.MenuItem
import android.widget.ImageView
import io.chthonic.igdb.poc.data.model.Order


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

    private val orderSelectedPublisher: PublishSubject<Order> by lazy {
        PublishSubject.create<Order>()
    }
    val orderSelectedObservable: Observable<Order>
        get() = orderSelectedPublisher.hide()

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

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener by lazy {
        object: BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val order = when (item.itemId) {
                    R.id.nav_pop -> Order.POPULARITY
                    R.id.nav_user -> Order.USER_REVIEW
                    R.id.nav_critic -> Order.CRITIC_REVIEW
                    else -> throw RuntimeException("OnNavigationItemReselectedListener: item id ${item.itemId} is not supported")
                }
                orderSelectedPublisher.onNext(order)
                return true
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

        rootView.bottom_nav.setOnNavigationItemSelectedListener(navListener)

        if (UiUtils.isHorizontal(rootView.resources)) {
            rootView.app_bar.setExpanded(false)
        }
    }

    override fun onDestroy() {
        // clear mem
        listView.clearOnScrollListeners()
        rootView.pullToRefresh.setPtrHandler(null)
        rootView.app_bar.removeOnOffsetChangedListener(appBarOnChangeListener)
        rootView.bottom_nav.setOnNavigationItemReselectedListener(null)
        super.onDestroy()
    }


    override fun hideLoading() {
        rootView.pullToRefresh.refreshComplete()
        super.hideLoading()
    }


    fun updateOrderSelection(order: Order) {
        rootView.bottom_nav.setOnNavigationItemSelectedListener(null)
        rootView.bottom_nav.selectedItemId = when (order) {
            Order.POPULARITY -> R.id.nav_pop
            Order.USER_REVIEW -> R.id.nav_user
            Order.CRITIC_REVIEW -> R.id.nav_critic
        }
        rootView.bottom_nav.setOnNavigationItemSelectedListener(navListener)
    }

    fun showOffline() {
        showError(activity.getString(R.string.error_no_internet))
    }

    fun showError(errorMsg: String) {
        rootView.pullToRefresh.refreshComplete()
        if ((adapter.errorMessage != errorMsg) || !adapter.hasValidData) {
            adapter.errorMessage = errorMsg
            adapter.notifyDataSetChanged()
        }
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
        Timber.d("updateGames: hadEmptyState = $hadEmptyState, oldSize = $oldSize, newSize = $newSize")

        if (!hadEmptyState && (oldSize == newSize) && (nuGames == getGames())) {
            Timber.d("updateGames: return")
            return
        }
        adapter.gameList = nuGames


        if ((oldSize == 0) && (newSize == 0)) {
            adapter.notifyItemRangeChanged(0, adapter.itemCount)

        } else if (oldSize == 0) {
            if (hadEmptyState) {
                Timber.d("updateGames: notifyItemRangeChanged, notifyItemRangeInserted")
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

        listView.scrollToPosition(0)
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