package ru.otus.cryptosample.coins.feature.adapter.parent

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarouselLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(
    context,
    orientation,
    reverseLayout
) {
    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
        lp.width = (width / COUNT_ITEMS_IN_SCREEN).toInt()
        return true
    }

    companion object {
        private const val COUNT_ITEMS_IN_SCREEN = 2.5f
    }
}