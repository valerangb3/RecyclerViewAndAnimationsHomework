package ru.otus.cryptosample.coins.feature.adapter.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.coins.feature.CoinCategoryState
import ru.otus.cryptosample.coins.feature.adapter.child.CoinsAdapter
import ru.otus.cryptosample.coins.feature.adapter.child.CoinsAdapterItem
import ru.otus.cryptosample.databinding.ItemRvBinding

enum class ListType {
    HORIZONTAL, VERTICAL;
}

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var items = listOf<CategoryAdapterItem>()
    private var listType: ListType = ListType.VERTICAL

    fun setListType(type: ListType) {
        listType = type
    }

    fun setData(categories: List<CoinCategoryState>) {
        //TODO нужно хранить исходны список. з.ы. flat list не подойдет
        val adapterItems = mutableListOf<CategoryAdapterItem>()

        categories.forEach { category ->
            adapterItems.add(
                CategoryAdapterItem.CategoryItem(
                    category = category.copy()
                )
            )
        }

        items = adapterItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class CategoryViewHolder(private val binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        val coinsAdapter = CoinsAdapter()

        fun bind(coinCategoryState: CategoryAdapterItem) {
            //todo долделать имплементацию определения какой будет режим отображения
            binding.childRv.adapter = coinsAdapter
            if (coinCategoryState is CategoryAdapterItem.CategoryItem) {
                coinsAdapter.setData(coinCategoryState.category)
            }
            binding.childRv.layoutManager = getLayoutManager()
        }

        private fun getLayoutManager(): RecyclerView.LayoutManager {
            val gridLayoutManager = GridLayoutManager(binding.root.context, 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (coinsAdapter.getItemViewType(position)) {
                        0 -> 2 // Category header spans full width
                        1 -> 1 // Coin item spans half width
                        else -> 1
                    }
                }
            }
            gridLayoutManager.orientation = RecyclerView.VERTICAL
            return gridLayoutManager
        }
    }
}