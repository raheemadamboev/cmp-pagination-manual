package xyz.teamgravity.cmppaginationmanual

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    @SerialName("products") val products: List<ProductDto>,
    @SerialName("total") val total: Long
)
