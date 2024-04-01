package keyValueStore

import context.directory
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// TODO move
fun coroutineScope(scope: CoroutineScope) : CoroutineScope {
    checkNotNull(scope.coroutineContext[Job]) {
        "GlobalScope or CoroutineScope without a Job is not permitted."
    }
    return scope
}


class KeyValueStoreAdapter(
    private val scope: CoroutineScope,
    private val config: KeyValueStoreConfig,
) : CoroutineScope by coroutineScope(scope) {

    val expirationDuration: Duration get() = config.expirationDuration

    val store: KottageStorage by lazy {
        when (expirationDuration) {
            Duration.INFINITE -> KeyValueStoreFactory(scope, config, directory.document).storage()
            else -> KeyValueStoreFactory(scope, config, directory.cache).cache()
        }
    }

    suspend fun exists(storeKey: StoreKey): Boolean = store.exists(storeKey.key)

    suspend inline fun <reified T : Any> get(storeKey: StoreKey): T =
        store.get<StoreData<T>>(storeKey.key).data

    suspend inline fun <reified T : Any> getOrNull(storeKey: StoreKey): T? = try {
        get(storeKey)
    } catch (e: Exception) {
        null
    }

    suspend inline fun <reified T : Any> getIfNotExpired(storeKey: StoreKey): T =
        store.get<StoreData<T>>(storeKey.key).takeUnless { it.isExpired() }?.data
            ?: throw NoSuchElementException("Data has expired")

    suspend inline fun <reified T : Any> getOrNullIfExpired(storeKey: StoreKey): T? = try {
        getIfNotExpired(storeKey)
    } catch (e: Exception) {
        null
    }

    suspend inline fun <reified T : Any> put(
        storeKey: StoreKey,
        value: T,
        expiresIn: Duration,
    ) = store.put<StoreData<T>>(
        storeKey.key,
        storeData(value, expiresIn),
        when(expirationDuration) {
            Duration.ZERO,
            Duration.INFINITE -> null
            else -> expiresIn.coerceIn(1.seconds, expirationDuration)
        },
    )

    suspend fun invalidate(storeKey: StoreKey) {
        store.remove(storeKey.key)
    }

    suspend fun invalidateAll() {
        store.removeAll()
    }
}
