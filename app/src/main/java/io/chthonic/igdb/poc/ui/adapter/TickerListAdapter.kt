package io.chthonic.igdb.poc.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.igdb.poc.ui.model.TickerViewModel
import io.chthonic.igdb.poc.ui.viewholder.TickerHolder
import io.reactivex.subjects.PublishSubject


/**
 * Created by jhavatar on 3/28/2018.
 */
class TickerListAdapter(private val tickerSelectPublisher: PublishSubject<String>) : RecyclerView.Adapter<TickerHolder>() {

    var _items: List<TickerViewModel> = emptyList()
    var items: List<TickerViewModel>
        get() = _items
        set(nuItems: List<TickerViewModel>) {
            _items = nuItems.toList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerHolder {
        return TickerHolder(TickerHolder.createView(parent))
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    override fun onBindViewHolder(holder: TickerHolder, position: Int) {
        val item = items[position]
        holder.update(item)
        RxView.clicks(holder.itemView)
                .map {
                    item.code
                }
                .subscribe(tickerSelectPublisher)
    }
}