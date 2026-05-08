package ru.otus.cryptosample.coins.feature.adapter.common

import androidx.recyclerview.widget.DiffUtil
import ru.otus.cryptosample.coins.feature.adapter.child.CoinItem

class CoinDiffUtils(
    private val oldItems: List<CoinItem>,
    private val newItems: List<CoinItem>,
) : DiffUtil.Callback() {

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {

        return oldItems[oldPosition].coin == newItems[newPosition].coin
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldItems[oldPosition].coin.id == newItems[newPosition].coin.id
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return if (oldItems[oldItemPosition].coin.highlight != newItems[newItemPosition].coin.highlight) {
            "HIGHLIGHT_UPDATED"
        } else {
            super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }

}