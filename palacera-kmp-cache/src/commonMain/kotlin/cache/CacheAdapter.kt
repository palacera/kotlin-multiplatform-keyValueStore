package cache

import context.directory
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.put
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

// TODO move
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

    val expirationEnabled: Boolean get() = config.expirationEnabled
    val minExpireDuration: Duration get() = config.minExpireDuration
    val maxExpireDuration: Duration get() = config.maxExpireDuration

    val cache: KottageStorage by lazy {
        when (config.expirationEnabled) {
            true -> CacheFactory(scope, config, directory.cache).cache()
            false -> CacheFactory(scope, config, directory.document).storage()
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
        when {
            expirationEnabled && cachePolicy is CachePolicy.UntilExpires ->
                cachePolicy.duration.coerceIn(minExpireDuration, maxExpireDuration)
            else ->
                null
        }
    )

    suspend fun invalidate(cacheKey: CacheKey) {
        cache.remove(cacheKey.key)
    }

    suspend fun invalidateAll() {
        cache.removeAll()
    }
}
