package xyz.teamgravity.cmppaginationmanual

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual class HttpClientEngineFactory actual constructor() {
    actual fun create(): HttpClientEngine {
        return Darwin.create()
    }
}