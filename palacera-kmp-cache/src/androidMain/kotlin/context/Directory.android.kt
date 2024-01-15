package context

import com.ncorti.kotlin.template.library.android.applicationContext

actual val directory: Directory = with (applicationContext) {
    Directory(
        document = filesDir.absolutePath,
        application = filesDir.absolutePath,
        temp = cacheDir.absolutePath,
        cache = cacheDir.absolutePath,
    )
}
