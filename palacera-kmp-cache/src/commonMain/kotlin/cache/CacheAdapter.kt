package cache

import context.directory
import getKottageContext
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.put
import io.github.irgaly.kottage.strategy.KottageFifoStrategy
import io.github.irgaly.kottage.strategy.KottageLruStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json

fun coroutineScope(scope: CoroutineScope) : CoroutineScope {
    checkNotNull(scope.coroutineContext[Job]) {
        "GlobalScope or CoroutineScope without a Job is not permitted."
    }
    return scope
}


class CacheAdapter(
    private val scope: CoroutineScope,
    private val config: CacheConfig,
) : CoroutineScope by coroutineScope(scope) {

    val cache: KottageStorage by lazy {
        val kottage = Kottage(
            name = "cache",
            directoryPath = directory.cache,
            environment = KottageEnvironment(
                context = getKottageContext(),
            ),
            scope = scope,
            json = Json.Default,
        )
        kottage.cache(config.name) {
            strategy =
                when (config.strategy) {
                    CacheStrategy.FirstInFirstOut -> KottageFifoStrategy(config.maxEntries)
                    CacheStrategy.LeastRecentlyUsed -> KottageLruStrategy(config.maxEntries)
                }
            defaultExpireTime = config.defaultExpireTime
        }
    }

    suspend fun exists(cacheKey: CacheKey): Boolean = cache.exists(cacheKey.key)

    suspend inline fun <reified T : Any> get(cacheKey: CacheKey): T =
        cache.get<CacheData<T>>(cacheKey.key).data

    suspend inline fun <reified T : Any> getOrNull(cacheKey: CacheKey): T? = try {
        get(cacheKey)
    } catch (e: Exception) {
        null
    }

    suspend inline fun <reified T : Any> getIfNotExpired(cacheKey: CacheKey): T =
        cache.get<CacheData<T>>(cacheKey.key).takeUnless { it.isExpired() }?.data
            ?: throw NoSuchElementException("Cache expired")

    suspend inline fun <reified T : Any> getOrNullIfExpired(cacheKey: CacheKey): T? = try {
        getIfNotExpired(cacheKey)
    } catch (e: Exception) {
        null
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

    suspend fun invalidate(cacheKey: CacheKey) {
        cache.remove(cacheKey.key)
    }

    suspend fun invalidateAll() {
        cache.removeAll()
    }
}
