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
import cache.CacheAdapter
import cache.CacheConfig
import cache.CachePolicy
import cache.CachePolicyManager
import cache.cacheKey
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
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
                CacheAdapter(
                    scope,
                    CacheConfig(
                        name = "cache",
                        file = "cache",
                    )
                )
            )
        }

        LaunchedEffect(scope) {
//            launch {
//                neverCachedValue = cacheManager.resolve(CachePolicy.Never, cacheKey("cache", "never")) {
//                    delay(2000)
//                    "Never cached"
//                }
//            }

            launch {
                ifAvailableCachedValue = cacheManager.resolve(CachePolicy.IfAvailable, cacheKey("cache", "if-available")) {
                    println("asdf not cached")
                    //delay(2000)
                    "Cached if available"
                }
            }

//            launch {
//                refreshCachedValue = cacheManager.resolve(CachePolicy.Refresh, cacheKey("cache", "refresh")) {
//                    delay(2000)
//                    "Refresh cache"
//                }
//            }
//
//            launch {
//                expireCachedValue = cacheManager.resolve(CachePolicy.UntilExpires(10.seconds), cacheKey("cache", "until-expires")) {
//                    delay(2000)
//                    "Expires cache"
//                }
//            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(neverCachedValue)
            Text(ifAvailableCachedValue)
            Text(refreshCachedValue)
            Text(expireCachedValue)
        }
    }
}
