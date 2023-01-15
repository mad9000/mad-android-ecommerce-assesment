package mad.app.madandroidtestsolutions.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mad.app.madandroidtestsolutions.R
import mad.app.madandroidtestsolutions.service.catalog.ICatalogApiService
import mad.app.madandroidtestsolutions.util.NetworkHelper
import mad.app.plptest.CategoryQuery
import mad.app.plptest.CategoryRootQuery
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val iCatalogApiService: ICatalogApiService,
    networkHelper: NetworkHelper,
    application: Application,
) : AndroidViewModel(application) {


    private val _catState = MutableLiveData<ViewState<List<CategoryRootQuery.Child?>?>>()
    val catState: LiveData<ViewState<List<CategoryRootQuery.Child?>?>> = _catState

    private val _viewState = MutableLiveData<ViewState<List<CategoryQuery.Item?>?>>()
    val viewState: LiveData<ViewState<List<CategoryQuery.Item?>?>> = _viewState

    var itemProductPaging = 1

    private var categoryUUID: String? = null

    var categoryProductResponse: MutableList<CategoryQuery.Item?>? = null

    private var _networkDialog: MutableLiveData<String> = MutableLiveData()
    val networkDialog: LiveData<String> = _networkDialog

    init {
        if (networkHelper.isNetworkAvailable(application)) {
            subscribeToCategories()
        } else {
            _networkDialog.value = application.getString(R.string.no_internet_connection_error)
        }
    }

    fun subscribeToCategories() {
        viewModelScope.launch {
            _catState.postValue(ViewState.Loading())
            try {
                val result = iCatalogApiService.fetchRootCategory()
                _catState.postValue(ViewState.Success(result?.children))
            } catch (exc: ApolloException) {
                _catState.postValue(ViewState.Error(exc.message))
            }
        }
    }

    fun getItemProduct() {
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading())
            try {
                val productResult =
                    iCatalogApiService.getProductsForCategory(categoryUUID ?: "",
                        pageNumber = itemProductPaging,
                        pageSize = 20)
                itemProductPaging++
                if (categoryProductResponse == null) {
                    categoryProductResponse =
                        productResult?.items as MutableList<CategoryQuery.Item?>?
                } else {
                    val oldProduct = categoryProductResponse
                    val newProduct = productResult?.items
                    newProduct?.let { oldProduct?.addAll(it) }
                }
                _viewState.postValue(ViewState.Success(categoryProductResponse
                    ?: productResult?.items))
            } catch (ex: ApolloException) {
                _viewState.postValue(ViewState.Error(ex.message))
            }
        }
    }

    fun setItemProduct(uuid: String) {
        this.categoryUUID = uuid
        this.categoryProductResponse = null
        this.itemProductPaging = 1
        getItemProduct()
    }

    sealed class ViewState<T>(
        val value: T? = null,
        val message: String? = null,
    ) {
        class Success<T>(val data: T) : ViewState<T>(data)
        class Error<T>(message: String?, data: T? = null) : ViewState<T>(data, message)
        class Loading<T> : ViewState<T>()
    }
}