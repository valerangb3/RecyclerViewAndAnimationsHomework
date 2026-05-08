package ru.otus.cryptosample.coins.feature.adapter.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class FadeSideAnimator : DefaultItemAnimator() {
    private val pendingAdditions = mutableListOf<RecyclerView.ViewHolder>()
    private val pendingRemovals = mutableListOf<RecyclerView.ViewHolder>() // Список для удалений

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        holder.itemView.alpha = 0f
        holder.itemView.translationX = 100f
        pendingAdditions.add(holder)

        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {

        resetAnimation(holder)
        pendingRemovals.add(holder)
        return true
    }

    override fun runPendingAnimations() {
        val removals = pendingRemovals.toList()
        pendingRemovals.clear()
        for (holder in removals) {
            animateRemoveImpl(holder)
        }

        val additions = pendingAdditions.toList()
        pendingAdditions.clear()
        for (holder in additions) {
            animateAddImpl(holder)
        }

        super.runPendingAnimations()
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        view.animate()
            .alpha(0f)
            .translationX(-100f)
            .setDuration(removeDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    dispatchRemoveStarting(holder)
                }
                override fun onAnimationEnd(animation: Animator) {
                    view.alpha = 1f // Сброс для будущего переиспользования
                    view.translationX = 0f
                    dispatchRemoveFinished(holder) // Сообщаем системе об окончании
                }
            })
            .start()
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(addDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    dispatchAddStarting(holder)
                }
                override fun onAnimationEnd(animation: Animator) {
                    view.alpha = 1f
                    view.translationX = 0f
                    dispatchAddFinished(holder)
                }
            })
            .start()
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        item.itemView.animate().cancel()
        if (pendingAdditions.remove(item)) {
            resetAnimation(item)
            dispatchAddFinished(item)
        }
        if (pendingRemovals.remove(item)) {
            resetAnimation(item)
            dispatchRemoveFinished(item)
        }
        super.endAnimation(item)
    }

    private fun resetAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().cancel()
        holder.itemView.alpha = 1f
        holder.itemView.translationX = 0f
    }
}
