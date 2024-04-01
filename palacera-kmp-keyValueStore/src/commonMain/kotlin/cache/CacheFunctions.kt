package cache

import kotlin.time.Duration

fun CachePolicy.refresh(refresh: Boolean) : CachePolicy = when {
    refresh && this is CachePolicy.IfAvailable -> CachePolicy.Refresh(expiresIn = Duration.INFINITE)
    refresh && this is CachePolicy.UntilExpires -> CachePolicy.Refresh(expiresIn = expiresIn)
    else -> this
}
