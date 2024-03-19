package cache

import getKottageContext
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.strategy.KottageFifoStrategy
import io.github.irgaly.kottage.strategy.KottageLruStrategy
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

internal class CacheFactory(
    private val scope: CoroutineScope,
    private val config: CacheConfig,
    private val directoryPath: String,
) : CoroutineScope by scope {

    private fun kottage() : Kottage = Kottage(
        name = config.file,
        directoryPath = directoryPath,
        environment = KottageEnvironment(
            context = getKottageContext(),
        ),
        scope = scope,
        json = Json,
    )

    fun cache() : KottageStorage = kottage().cache("cache-${config.name}") {
        strategy =
            when (config.strategy) {
                CacheStrategy.FirstInFirstOut -> KottageFifoStrategy(config.maxEntries)
                CacheStrategy.LeastRecentlyUsed -> KottageLruStrategy(config.maxEntries)
            }
        defaultExpireTime = config.maxExpireDuration
    }

    fun storage() : KottageStorage = kottage().storage("storage-${config.name}") {
        defaultExpireTime = Duration.INFINITE
    }
}
