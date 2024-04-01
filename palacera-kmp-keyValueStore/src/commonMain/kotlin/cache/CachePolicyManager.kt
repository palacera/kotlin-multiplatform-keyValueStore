package cache

import keyValueStore.KeyValueStoreAdapter
import keyValueStore.StoreKey
import kotlin.time.Duration

class CachePolicyManager(
    val store: KeyValueStoreAdapter,
) {
    suspend inline fun <reified T : Any> resolve(
        policy: CachePolicy,
        cacheKey: String,
        fetchSourceData: () -> T,
    ): T = resolve(policy, StoreKey(cacheKey), fetchSourceData)

    suspend inline fun <reified T : Any> resolve(
        policy: CachePolicy,
        storeKey: StoreKey,
        fetchSourceData: () -> T,
    ): T = when (policy) {

        is CachePolicy.Never -> {
            fetchSourceData()
        }

        is CachePolicy.IfAvailable -> {
            try {
                store.get(storeKey) as T
            } catch (e: Exception) {
                fetchSourceData().also { data ->
                    store.put(storeKey, data, Duration.INFINITE)
                }
            }
        }

        is CachePolicy.Refresh -> {
            fetchSourceData().also { data ->
                store.put(storeKey, data, policy.expiresIn)
            }
        }

        is CachePolicy.UntilExpires -> {
            try {
                store.getIfNotExpired(storeKey) as T
            } catch (e: Exception) {
                fetchSourceData().also { data ->
                    store.put(storeKey, data, policy.expiresIn)
                }
            }
        }
    }
}
