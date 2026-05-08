package ru.otus.cryptosample.coins.feature.adapter.common

import androidx.recyclerview.widget.DiffUtil
import ru.otus.cryptosample.coins.feature.adapter.parent.CategoryAdapterItem

class CategoryDiffUtils(
    val oldList: List<CategoryAdapterItem>,
    val newList: List<CategoryAdapterItem>
) : DiffUtil.Callback() {
    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldCategory = oldList[oldPosition]
        val newCategory = newList[newPosition]

        return when (oldCategory) {
            is CategoryAdapterItem.Categories if newCategory is CategoryAdapterItem.Categories -> {
                oldCategory.data == newCategory.data
            }
            is CategoryAdapterItem.CategoryTitle if newCategory is CategoryAdapterItem.CategoryTitle -> {
                oldCategory.name == newCategory.name
            }
            else -> false
        }
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldCategory = oldList[oldPosition]
        val newCategory = newList[newPosition]

        return when (oldCategory) {
            is CategoryAdapterItem.Categories if newCategory is CategoryAdapterItem.Categories -> {
                oldCategory.name == newCategory.name
            }
            is CategoryAdapterItem.CategoryTitle if newCategory is CategoryAdapterItem.CategoryTitle -> {
                oldCategory.name == newCategory.name
            }
            else -> false
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldCategory = oldList[oldItemPosition]
        val newCategory = newList[newItemPosition]
        return if (oldCategory is CategoryAdapterItem.Categories && newCategory is CategoryAdapterItem.Categories) {
            if (oldCategory.data != newCategory.data) "INNER_DATA_CHANGED"
            else null
        } else null
    }
}