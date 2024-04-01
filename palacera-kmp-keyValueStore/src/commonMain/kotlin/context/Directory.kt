package context

expect val directory: Directory

data class Directory(
    val document: String,
    val application: String,
    val temp: String,
    val cache: String,
)
