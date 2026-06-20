import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    let bridge = IosImagePickerBridge.init()
    
    init() {
        KoinKt.doInitKoin(picker: bridge)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
