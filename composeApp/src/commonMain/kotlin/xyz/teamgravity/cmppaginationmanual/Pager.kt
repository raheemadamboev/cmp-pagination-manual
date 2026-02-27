package xyz.teamgravity.cmppaginationmanual

class Pager<Key, Item>(
    private val initialKey: Key,
    private val onLoading: suspend (isLoading: Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<Item>,
    private val onGenerateNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (error: Throwable?) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val onEndReached: (currentKey: Key, result: Item) -> Boolean
) {

    private var currentKey: Key = initialKey
    private var isRequesting: Boolean = false
    private var isEnded: Boolean = false

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    suspend fun loadNextItems() {
        if (isRequesting || isEnded) return

        isRequesting = true
        onLoading(true)
        val result = onRequest(currentKey)
        isRequesting = false

        val item = result.getOrElse { error ->
            onError(error)
            onLoading(false)
            return
        }

        currentKey = onGenerateNextKey(currentKey, item)

        onSuccess(item, currentKey)

        onLoading(false)

        isEnded = onEndReached(currentKey, item)
    }

    fun reset() {
        currentKey = initialKey
        isEnded = false
    }
}