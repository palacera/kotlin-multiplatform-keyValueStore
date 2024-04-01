package keyValueStore

sealed interface PurgeStrategy {
    data object FirstInFirstOut : PurgeStrategy
    data object LeastRecentlyUsed : PurgeStrategy
}
