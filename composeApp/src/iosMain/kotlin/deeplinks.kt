import com.francescobottino.thehubproject.koin
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("onDeeplinkReceived")
fun onDeeplinkReceived(url: String) {
    koin.get<DeepLinkHandler>().onDeepLinkReceived(url)
}