package cache

fun CachePolicy.refresh(refresh: Boolean) : CachePolicy = when {
    refresh && this !is CachePolicy.Never -> CachePolicy.Refresh
    else -> this
}
