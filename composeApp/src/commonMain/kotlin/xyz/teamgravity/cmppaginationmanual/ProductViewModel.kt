package xyz.teamgravity.cmppaginationmanual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(
    private val api: ProductApi
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 10
    }

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val pager: Pager<Int, ProductsResponse> = Pager(
        initialKey = 0,
        onLoading = { isLoading ->
            _isLoading.emit(isLoading)
        },
        onRequest = { nextKey ->
            api.getProducts(
                page = nextKey,
                pageSize = PAGE_SIZE
            )
        },
        onGenerateNextKey = { currentKey, _ ->
            currentKey + 1
        },
        onError = { throwable ->
            _error.emit(throwable?.message)
        },
        onSuccess = { result, _ ->
            _products.update { it + result.products }
        },
        onEndReached = { currentKey, result ->
            (currentKey * PAGE_SIZE) >= result.total
        }
    )

    init {
        onLoadNexItems()
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onLoadNexItems() {
        viewModelScope.launch {
            pager.loadNextItems()
        }
    }
}