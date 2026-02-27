package xyz.teamgravity.cmppaginationmanual

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual class HttpClientEngineFactory actual constructor() {
    actual fun create(): HttpClientEngine {
        return OkHttp.create()
    }
}