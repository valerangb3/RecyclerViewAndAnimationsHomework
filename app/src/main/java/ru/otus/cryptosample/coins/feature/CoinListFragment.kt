package ru.otus.cryptosample.coins.feature

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.otus.cryptosample.CoinsSampleApp
import ru.otus.cryptosample.coins.feature.adapter.common.ViewTypes
import ru.otus.cryptosample.coins.feature.adapter.parent.CategoryAdapter
import ru.otus.cryptosample.coins.feature.adapter.common.CategoryDiffUtils
import ru.otus.cryptosample.coins.feature.adapter.common.FadeSideAnimator
import ru.otus.cryptosample.coins.feature.adapter.parent.ListType
import ru.otus.cryptosample.coins.feature.di.DaggerCoinListComponent
import ru.otus.cryptosample.databinding.FragmentCoinListBinding
import javax.inject.Inject

class CoinListFragment : Fragment() {

    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: CoinListViewModelFactory

    private val viewModel: CoinListViewModel by viewModels { factory }

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val appComponent = (activity?.applicationContext as CoinsSampleApp).appComponent

        DaggerCoinListComponent.factory()
            .create(appComponent)
            .inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipToggle()
        subscribeUI()
    }

    private fun setupRecyclerView() {
        val sharedViewPool = RecyclerView.RecycledViewPool()
        sharedViewPool.setMaxRecycledViews(ViewTypes.TITLE_VIEW, 6)
        sharedViewPool.setMaxRecycledViews(ViewTypes.ITEM_VIEW, 20)
        sharedViewPool.setMaxRecycledViews(ViewTypes.LIST_VIEW_VERTICAL, 6)
        sharedViewPool.setMaxRecycledViews(ViewTypes.LIST_VIEW_HORIZONTAL, 6)
        categoryAdapter = CategoryAdapter(sharedViewPool, ViewTypes)

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = 2
        }

        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = categoryAdapter
            itemAnimator = FadeSideAnimator().apply {
                addDuration = 700
                removeDuration = 700
            }
            setRecycledViewPool(sharedViewPool)
        }
    }

    private fun setupChipToggle() {
        binding.highlightChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onHighlightMoversToggled(isChecked)
        }

        binding.showAllChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onShowAllToggled(isChecked)
        }
    }

    private fun subscribeUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: CoinsScreenState) {
        val listType = if (state.showAll)
            ListType.HORIZONTAL
        else
            ListType.VERTICAL
        val oldCategories = categoryAdapter.items
        categoryAdapter.setData(state.categories, listType)
        val newCategories = categoryAdapter.items
        val categoriesDiffUtilsCallback = CategoryDiffUtils(oldCategories, newCategories)
        val diff = DiffUtil.calculateDiff(categoriesDiffUtilsCallback)
        diff.dispatchUpdatesTo(categoryAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
