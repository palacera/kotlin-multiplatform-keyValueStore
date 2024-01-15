package cache

class CachePolicyManager(
    val cache: CacheAdapter
) {
    suspend inline fun <reified T: Any> resolve(
        policy: CachePolicy,
        cacheKey: CacheKey,
        fetchSourceData: () -> T,
    ): T {

        return when (policy) {
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
                    cache.getUnlessExpired(cacheKey) as T
                } catch (e: Exception) {
                    fetchSourceData().also { data ->
                        cache.put(cacheKey, data, policy)
                    }
                }
            }
        }
    }
}
