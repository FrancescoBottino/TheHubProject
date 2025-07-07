import com.francescobottino.thehubproject.initKoinForIOS
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("InitApp")
fun initAppForIOS() {
    Napier.base(DebugAntilog())
    initKoinForIOS()
}