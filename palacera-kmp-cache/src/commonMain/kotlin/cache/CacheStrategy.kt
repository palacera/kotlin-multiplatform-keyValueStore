package cache

sealed interface CacheStrategy {
    data object FirstInFirstOut : CacheStrategy
    data object LeastRecentlyUsed : CacheStrategy
}
