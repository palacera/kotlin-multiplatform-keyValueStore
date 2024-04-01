package keyValueStore

import getKottageContext
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.strategy.KottageFifoStrategy
import io.github.irgaly.kottage.strategy.KottageLruStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import kotlin.time.Duration

internal class KeyValueStoreFactory(
    private val scope: CoroutineScope,
    private val config: KeyValueStoreConfig,
    private val directoryPath: String,
) {
    private fun kottage(): Kottage = Kottage(
        name = config.file,
        directoryPath = directoryPath,
        environment = KottageEnvironment(
            context = getKottageContext(),
        ),
        scope = scope,
        json = Json.Default,
    )

    fun cache() : KottageStorage = kottage().cache("cache:${config.name}") {
        strategy =
            when (config.purgeStrategy) {
                PurgeStrategy.FirstInFirstOut -> KottageFifoStrategy(config.maxEntries)
                PurgeStrategy.LeastRecentlyUsed -> KottageLruStrategy(config.maxEntries)
            }
        defaultExpireTime = config.expirationDuration
    }

    fun storage() : KottageStorage = kottage().storage("storage:${config.name}") {
        defaultExpireTime = Duration.INFINITE
    }
}
