package mad.app.madandroidtestsolutions.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import mad.app.madandroidtestsolutions.databinding.CategoryFragmentBinding
import mad.app.madandroidtestsolutions.ui.adapter.CategoryAdapter
import mad.app.plptest.CategoryQuery
import mad.app.plptest.CategoryRootQuery

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var _binding: CategoryFragmentBinding

    private lateinit var productAdapter: CategoryAdapter

    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CategoryFragmentBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        viewModel.catState.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                handleCategoryResults(it)
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { products ->
            products?.let { product ->
                handleProductResults(product)
            }
        }

        _binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tag = it.tag
                    if (tag != null) {
                        viewModel.setItemProduct(tag.toString())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        productAdapter.setOnItemClickListener {

        }
    }

    private fun setUpRecyclerView() {
        productAdapter = CategoryAdapter()
        _binding.recyclerView.apply {
            adapter = productAdapter
            addOnScrollListener(this@CategoryFragment.scrollListener)
        }
    }

    private fun handleProductResults(product: CategoryViewModel.ViewState<List<CategoryQuery.Item?>?>) {
        when (product) {
            is CategoryViewModel.ViewState.Success -> {
                val data = product.data
                if (data.isNullOrEmpty()) {
                    _binding.recyclerView.isVisible = false
                    _binding.emptyMessage.isVisible = true
                } else {
                    productAdapter.differ.submitList(data)
                    _binding.recyclerView.isVisible = true
                    _binding.emptyMessage.isVisible = false
                    val totalPages = product.data.size / 20 + 2
                    isLastPage = viewModel.itemProductPaging == totalPages
                    if (isLastPage) {
                        _binding.recyclerView.setPadding(0, 0, 0, 0)
                    }
                }
                _binding.loader.hide()
                isLoading = false
            }
            is CategoryViewModel.ViewState.Loading -> {
                _binding.loader.show()
                isLoading = true
            }
            is CategoryViewModel.ViewState.Error -> {
                _binding.loader.hide()
                isLoading = false
            }
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20
            val shouldPage = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPage) {
                viewModel.getItemProduct()
                isScrolling = false
            }
        }
    }

    private fun handleCategoryResults(viewState: CategoryViewModel.ViewState<List<CategoryRootQuery.Child?>?>) {
        when (viewState) {
            is CategoryViewModel.ViewState.Success -> {
                viewState.data?.let { rootCategory ->
                    for (category in rootCategory) {
                        _binding.tabLayout.addTab(_binding.tabLayout.newTab()
                            .setText(category?.name).setTag(category?.uid ?: ""))
                    }
                }
            }
            is CategoryViewModel.ViewState.Loading -> {
                //Display loading indicator
            }
            is CategoryViewModel.ViewState.Error -> {
                Log.d("Error loading categories", viewState.message.toString())
                //Display error message
            }
        }
    }
}