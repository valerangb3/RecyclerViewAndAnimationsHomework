package ru.otus.cryptosample.coins.feature.adapter.parent

import ru.otus.cryptosample.coins.feature.CoinCategoryState

sealed interface CategoryAdapterItem {
    data class CategoryItem(val category: CoinCategoryState) : CategoryAdapterItem
}