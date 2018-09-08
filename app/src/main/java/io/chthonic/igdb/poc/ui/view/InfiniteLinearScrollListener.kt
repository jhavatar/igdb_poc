package io.chthonic.igdb.poc.ui.view

import android.support.v7.widget.RecyclerView

abstract class InfiniteLinearScrollListener(val layoutManager: android.support.v7.widget.LinearLayoutManager,
                                            val pageSize: Int? = null,
                                            val preemptThresholdCount: Int = 0) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (canLoadMoreItems()) {
            onScrolledToEnd(layoutManager.findFirstVisibleItemPosition())
        }
    }

    protected fun canLoadMoreItems(): Boolean {
        // number attached to recyclerView
        val visibleItemsCount = layoutManager.getChildCount()

        // adapter position of the first visible view
        val pastVisibleItemsCount = layoutManager.findFirstVisibleItemPosition()

        // number items in adapter
        val totalItemsCount = layoutManager.getItemCount()

        val lastDesiredItemIsShown = ((visibleItemsCount + pastVisibleItemsCount) >= (totalItemsCount - preemptThresholdCount))

        return lastDesiredItemIsShown && (totalItemsCount >= (pageSize ?: 1))
    }

    abstract fun onScrolledToEnd(firstVisibleItemPosition: Int)
}