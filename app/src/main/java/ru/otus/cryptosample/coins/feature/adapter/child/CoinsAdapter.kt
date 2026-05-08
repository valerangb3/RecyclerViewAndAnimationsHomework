package ru.otus.cryptosample.coins.feature.adapter.child

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.coins.feature.CoinState
import ru.otus.cryptosample.coins.feature.adapter.common.CoinDiffUtils
import ru.otus.cryptosample.coins.feature.adapter.common.ViewTypes
import ru.otus.cryptosample.databinding.ItemCoinBinding

class CoinsAdapter(
    private val viewTypes: ViewTypes
) : RecyclerView.Adapter<CoinViewHolder>() {
    var isHorizontal: Boolean = false
    var items = listOf<CoinItem>()
        private set

    fun setData(coins: List<CoinState>) {
        val oldCoins = items
        val adapterItems = mutableListOf<CoinItem>()

        coins.forEach { coinState ->
            adapterItems.add(CoinItem(coinState))
        }

        val coinsDiffUtilsCallback = CoinDiffUtils(oldCoins, adapterItems.toList())
        items = adapterItems
        val diff = DiffUtil.calculateDiff(coinsDiffUtilsCallback)
        diff.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypes.ITEM_VIEW
    }
    
    override fun getItemCount(): Int = items.size
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        return CoinViewHolder(
            ItemCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams

        if (isHorizontal) {
            // Режим карусели: вычисляем 2.5 карточки
            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
            layoutParams.width = (screenWidth / 2.5).toInt()
        } else {
            // Режим сетки: карточка должна занимать всю выделенную ей ячейку
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        holder.itemView.layoutParams = layoutParams
        val item = items[position]
        holder.bind(item.coin)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int, payloads: List<Any>) {
        val item = items[position]
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for (payload in payloads) {
                if (payload == "HIGHLIGHT_UPDATED") {
                    holder.badgeUpdate(item.coin.highlight)
                }
            }
        }
    }
}