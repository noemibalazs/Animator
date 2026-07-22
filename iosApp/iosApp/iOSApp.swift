import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @State private var sharedURL: URL? = nil

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
                .onOpenURL { url in
                    print("Animator - received URL: \(url)")

                    if url.scheme == "animatorshare" {
                        self.sharedURL = url
                        delegate.application(
                            UIApplication.shared,
                            open: url,
                            options: [:]
                        )
                    }
                }
        }
    }
}
