import com.francescobottino.thehubproject.initKoinForIOS
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("InitApp")
fun initAppForIOS() {
    initKoinForIOS()
}