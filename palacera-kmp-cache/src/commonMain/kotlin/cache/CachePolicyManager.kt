package cache

class CachePolicyManager(
    val cache: CacheAdapter,
) {
    suspend inline fun <reified T : Any> resolve(
        policy: CachePolicy,
        cacheKey: String,
        fetchSourceData: () -> T,
    ): T = resolve(policy, CacheKey(cacheKey), fetchSourceData)

    suspend inline fun <reified T : Any> resolve(
        policy: CachePolicy,
        cacheKey: CacheKey,
        fetchSourceData: () -> T,
    ): T = when (policy) {

        is CachePolicy.Never -> {
            fetchSourceData()
        }

        is CachePolicy.IfAvailable -> {
            try {
                cache.get(cacheKey) as T
            } catch (e: Exception) {
                fetchSourceData().also { data ->
                    cache.put(cacheKey, data, policy)
                }
            }
        }

        is CachePolicy.Refresh -> {
            fetchSourceData().also { data ->
                cache.put(cacheKey, data, policy)
            }
        }

        is CachePolicy.UntilExpires -> {
            try {
                cache.getIfNotExpired(cacheKey) as T
            } catch (e: Exception) {
                fetchSourceData().also { data ->
                    cache.put(cacheKey, data, policy)
                }
            }
        }
    }
}
