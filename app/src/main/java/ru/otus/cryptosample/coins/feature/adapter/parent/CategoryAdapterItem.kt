package ru.otus.cryptosample.coins.feature.adapter.parent

import ru.otus.cryptosample.coins.feature.CoinState

sealed interface CategoryAdapterItem {
    data class Categories(val name: String, val data: List<CoinState>) : CategoryAdapterItem
    data class CategoryTitle(val name: String) : CategoryAdapterItem
}