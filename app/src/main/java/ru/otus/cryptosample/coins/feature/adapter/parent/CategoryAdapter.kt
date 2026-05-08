package ru.otus.cryptosample.coins.feature.adapter.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.coins.feature.CoinCategoryState
import ru.otus.cryptosample.coins.feature.CoinState
import ru.otus.cryptosample.coins.feature.adapter.child.CategoryHeaderViewHolder
import ru.otus.cryptosample.coins.feature.adapter.child.CoinsAdapter
import ru.otus.cryptosample.coins.feature.adapter.common.FadeSideAnimator
import ru.otus.cryptosample.coins.feature.adapter.common.ViewTypes
import ru.otus.cryptosample.databinding.ItemCategoryHeaderBinding
import ru.otus.cryptosample.databinding.ItemRvBinding

enum class ListType {
    HORIZONTAL, VERTICAL;
}

class CategoryAdapter(
    private val viewPool: RecyclerView.RecycledViewPool,
    private val viewTypes: ViewTypes
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val THRESHOLD = 10
    }

    var items = listOf<CategoryAdapterItem>()
        private set
    var categories: List<CoinCategoryState> = emptyList()
        private set
    private var listType: ListType = ListType.VERTICAL
    private val innerAdapters = mutableMapOf<String, CoinsAdapter>()
    private val innerAnimators = mutableMapOf<String, FadeSideAnimator>()

    private fun getOrCreateAnimator(name: String): FadeSideAnimator {
        return innerAnimators.getOrPut(name) {
            FadeSideAnimator().apply { addDuration = 700; removeDuration = 700 }
        }
    }

    fun setData(categories: List<CoinCategoryState>, type: ListType) {
        listType = type
        this.categories = categories
        val adapterItems = mutableListOf<CategoryAdapterItem>()
        categories.forEach { category ->
            adapterItems.add(CategoryAdapterItem.CategoryTitle(category.name))
            adapterItems.add(
                CategoryAdapterItem.Categories(
                    data = category.coins.map { it },
                    name = category.name
                )
            )
        }

        items = adapterItems
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is CategoryAdapterItem.CategoryTitle -> viewTypes.TITLE_VIEW
            is CategoryAdapterItem.Categories -> {
                if (isHorizontalScroll(item.data))
                    viewTypes.LIST_VIEW_HORIZONTAL
                else
                    viewTypes.LIST_VIEW_VERTICAL
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun isHorizontalScroll(data: List<CoinState>): Boolean {
        return listType == ListType.HORIZONTAL && data.size > THRESHOLD
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryHorizontalViewHolder -> {
                val categories = items[position] as CategoryAdapterItem.Categories
                val innerAdapter = innerAdapters.getOrPut(categories.name) { CoinsAdapter(ViewTypes) }
                holder.bind(
                    innerAdapter,
                    categories.data,
                    isHorizontalScroll(categories.data)
                )
            }
            is CategoryVerticalViewHolder -> {
                val categories = items[position] as CategoryAdapterItem.Categories
                val innerAdapter = innerAdapters.getOrPut(categories.name) { CoinsAdapter(ViewTypes) }
                val fa = getOrCreateAnimator(categories.name)
                holder.bind(
                    innerAdapter,
                    categories.data,
                    fa,
                    isHorizontalScroll(categories.data)
                )
            }
            is CategoryHeaderViewHolder -> {
                val categoryTitle = items[position] as CategoryAdapterItem.CategoryTitle
                holder.bind(categoryTitle.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypes.LIST_VIEW_VERTICAL -> {
                val vBinding = ItemRvBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).apply {
                    childRv.apply {
                        setRecycledViewPool(viewPool)
                    }
                }
                CategoryVerticalViewHolder(vBinding)
            }
            viewTypes.LIST_VIEW_HORIZONTAL -> {
                val vBinding = ItemRvBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).apply {
                    childRv.apply {
                        setRecycledViewPool(viewPool)
                    }
                }
                CategoryHorizontalViewHolder(vBinding)
            }
            viewTypes.TITLE_VIEW -> CategoryHeaderViewHolder(
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any?>,
    ) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                if (payload == "INNER_DATA_CHANGED") {
                    val categories = items[position] as CategoryAdapterItem.Categories
                    val innerAdapter = innerAdapters.getOrPut(categories.name) { CoinsAdapter(ViewTypes) }
                    if (holder is CategoryHorizontalViewHolder) {
                        holder.bind(
                            innerAdapter,
                            categories.data,
                            isHorizontalScroll(categories.data)
                        )
                    }
                    if (holder is CategoryVerticalViewHolder) {
                        innerAdapter.setData(categories.data)
                    }
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }


    class CategoryVerticalViewHolder(private val binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            coinsAdapter: CoinsAdapter,
            coins: List<CoinState>,
            fadeSideAnimator: FadeSideAnimator,
            isHorizontal: Boolean
        ) {
            if (binding.childRv.adapter != coinsAdapter) {
                binding.childRv.adapter = coinsAdapter
            }
            coinsAdapter.isHorizontal = isHorizontal
            if (binding.childRv.itemAnimator != fadeSideAnimator) {
                binding.childRv.itemAnimator = fadeSideAnimator
            }
            if (binding.childRv.layoutManager !is GridLayoutManager) {
                binding.childRv.itemAnimator?.endAnimations()
                binding.childRv.apply {
                    layoutManager = GridLayoutManager( binding.root.context, 2)
                }
            }
            coinsAdapter.setData(coins)
        }
    }

    class CategoryHorizontalViewHolder(private val binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            coinsAdapter: CoinsAdapter,
            coins: List<CoinState>,
            isHorizontal: Boolean
        ) {

            if (binding.childRv.adapter != coinsAdapter) {
                binding.childRv.adapter = coinsAdapter
            }
            coinsAdapter.isHorizontal = isHorizontal

            if (binding.childRv.layoutManager !is CarouselLayoutManager) {
                binding.childRv.itemAnimator?.endAnimations()
                binding.childRv.apply {
                    layoutManager = CarouselLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }

            coinsAdapter.setData(coins)
        }
    }
}