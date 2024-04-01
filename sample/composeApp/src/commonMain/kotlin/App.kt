import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import keyValueStore.KeyValueStoreAdapter
import keyValueStore.KeyValueStoreConfig
import cache.CachePolicy
import cache.CachePolicyManager
import keyValueStore.storeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun App() {

    MaterialTheme {
        val scope = rememberCoroutineScope()

        var neverCachedValue by remember { mutableStateOf("loading") }
        var ifAvailableCachedValue by remember { mutableStateOf("loading") }
        var refreshCachedValue by remember { mutableStateOf("loading") }
        var expireCachedValue by remember { mutableStateOf("loading") }

        val cacheManager = remember {
            CachePolicyManager(
                KeyValueStoreAdapter(
                    scope,
                    KeyValueStoreConfig(
                        name = "cacheStore",
                    )
                )
            )
        }

        LaunchedEffect(Unit) {
            launch {
                neverCachedValue = cacheManager.resolve(CachePolicy.Never, storeKey("cache", "never")) {
                    delay(2000)
                    "Never cached"
                }
            }

            launch {
                ifAvailableCachedValue = cacheManager.resolve(CachePolicy.IfAvailable, storeKey("cache", "if-available")) {
                    delay(2000)
                    "Cached if available"
                }
            }

            launch {
                refreshCachedValue = cacheManager.resolve(CachePolicy.Refresh(), storeKey("cache", "refresh")) {
                    delay(2000)
                    "Refresh cache"
                }
            }

            launch {
                expireCachedValue = cacheManager.resolve(CachePolicy.UntilExpires(10.seconds), storeKey("cache", "until-expires")) {
                    delay(2000)
                    "Expires cache"
                }
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(neverCachedValue)
            Text(ifAvailableCachedValue)
            Text(refreshCachedValue)
            Text(expireCachedValue)
        }
    }
}
