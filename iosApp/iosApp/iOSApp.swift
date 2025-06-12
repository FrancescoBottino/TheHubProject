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
        }
    }
}
