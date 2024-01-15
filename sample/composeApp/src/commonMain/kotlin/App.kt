import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val greeting = remember { Greeting().greet() }
        val scope = rememberCoroutineScope()

        var neverCachedValue by remember { mutableStateOf("loading") }
        var ifAvailableCachedValue by remember { mutableStateOf("loading") }
        var refreshCachedValue by remember { mutableStateOf("loading") }
        var expireCachedValue by remember { mutableStateOf("loading") }

        val cacheAdapter = remember {
            CacheAdapter(
                scope,
                CacheConfig(
                    name = "cache",
                    file = "cache",
                )
            )
        }


        val cacheManager = remember { CachePolicyManager(cacheAdapter) }

        scope.launch {
            launch {
                neverCachedValue = cacheManager.resolve(CachePolicy.Never, cacheKey("cache", "never")) {
                    delay(2000)
                    "Never cached"
                }
            }

            launch {
                ifAvailableCachedValue = cacheManager.resolve(CachePolicy.IfAvailable, cacheKey("cache", "if-available")) {
                    delay(2000)
                    "Cached if available"
                }
            }

            launch {
                refreshCachedValue = cacheManager.resolve(CachePolicy.Refresh, cacheKey("cache", "refresh")) {
                    delay(2000)
                    "Refresh cache"
                }
            }

            launch {
                expireCachedValue = cacheManager.resolve(CachePolicy.UntilExpires(10.seconds), cacheKey("cache", "until-expires")) {
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

            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource("compose-multiplatform.xml"), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}
