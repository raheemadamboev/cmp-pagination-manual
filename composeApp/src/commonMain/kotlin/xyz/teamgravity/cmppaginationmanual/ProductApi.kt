package xyz.teamgravity.cmppaginationmanual

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class ProductApi(
    private val client: HttpClient
) {

    ///////////////////////////////////////////////////////////////////////////
    // Get
    ///////////////////////////////////////////////////////////////////////////

    suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductsResponse> {
        delay(2.seconds) // artificial delay to see the loading of the pagination - remove in production

        val response = try {
            val response = client.get(
                urlString = "https://dummyjson.com/products?select=title,price"
            ) {
                contentType(ContentType.Application.Json)
                parameter("limit", pageSize)
                parameter("skip", page * pageSize)
            }
            response.body<ProductsResponse>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Result.failure(e)
        }

        return Result.success(response)
    }
}