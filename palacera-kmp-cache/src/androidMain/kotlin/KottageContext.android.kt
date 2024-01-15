import com.ncorti.kotlin.template.library.android.applicationContext
import io.github.irgaly.kottage.platform.contextOf

actual fun getKottageContext() = contextOf(applicationContext)
