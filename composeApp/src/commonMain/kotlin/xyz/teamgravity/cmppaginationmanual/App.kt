package xyz.teamgravity.cmppaginationmanual

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewmodel = viewModel(
            initializer = {
                ProductViewModel(
                    api = ProductApi(
                        client = HttpClient(
                            engine = HttpClientEngineFactory().create()
                        ) {
                            install(Logging) {
                                logger = Logger.SIMPLE
                                level = LogLevel.ALL
                            }
                            install(ContentNegotiation) {
                                json(
                                    json = Json { ignoreUnknownKeys = true }
                                )
                            }
                        }
                    )
                )
            }
        )

        val products by viewmodel.products.collectAsStateWithLifecycle()
        val isLoading by viewmodel.isLoading.collectAsStateWithLifecycle()
        val state = rememberLazyListState()

        LaunchedEffect(
            key1 = products,
            block = {
                snapshotFlow {
                    state.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                }.distinctUntilChanged().collect { lastVisibleIndex ->
                    if (lastVisibleIndex == products.lastIndex) {
                        viewmodel.onLoadNexItems()
                    }
                }
            }
        )

        Scaffold { padding ->
            LazyColumn(
                state = state,
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = product.title,
                            fontSize = 18.sp
                        )
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        Text(
                            text = "$ ${product.price}"
                        )
                    }
                }
                if (isLoading) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}