package cache

import context.directory
import getKottageContext
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.put
import io.github.irgaly.kottage.strategy.KottageFifoStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

class CacheAdapter(
    val coroutineScope: CoroutineScope,
    val config: CacheConfig,
) {
    val cache: KottageStorage by lazy {
        val kottage =
            Kottage(
                name = "cache",
                directoryPath =
                    when (config.isTemporary) {
                        true -> directory.cache
                        false -> directory.document
                    },
                environment =
                    KottageEnvironment(
                        context = getKottageContext(),
                    ),
                scope = coroutineScope,
                json = Json.Default,
            )
        kottage.cache(config.name) {
            strategy =
                when (config.strategy) {
                    CacheStrategy.FirstInFirstOut -> KottageFifoStrategy(config.maxEntries)
                    CacheStrategy.LeastRecentlyUsed -> KottageFifoStrategy(config.maxEntries)
                }
            defaultExpireTime = config.defaultExpireTime
        }
    }

    suspend inline fun <reified T : Any> put(
        cacheKey: CacheKey,
        value: T,
        cachePolicy: CachePolicy,
    ) = cache.put<CacheData<T>>(
        cacheKey.key,
        cacheData(value, cachePolicy),
        cachePolicy.getExpireDuration(),
    )

    suspend inline fun <reified T : Any> get(cacheKey: CacheKey): T = cache.get<CacheData<T>>(cacheKey.key).data

    suspend inline fun <reified T : Any> getUnlessExpired(cacheKey: CacheKey): T =
        cache.get<CacheData<T>>(cacheKey.key).takeUnless { it.isExpired() }?.data
            ?: throw Exception("Cache expired") // TODO create custom exception

    suspend fun exists(cacheKey: CacheKey): Boolean = cache.exists(cacheKey.key)

    suspend fun invalidate(cacheKey: CacheKey) = cache.remove(cacheKey.key)

    suspend fun invalidateAll() = cache.removeAll()
}
