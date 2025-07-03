import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        OnAppInitKt.InitApp()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    DeeplinksKt.onDeeplinkReceived(url: url.absoluteString)
                }
        }
    }
}
