import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    let imageBridge = IosImagePickerBridge.init()
    let cameraBridge = IosCameraActionBridge.init()

    init() {
        KoinKt.doInitKoin(
            picker: imageBridge,
            actionHandler: cameraBridge
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
