import Foundation
import ComposeApp
import UIKit

class IosCameraActionBridge: NSObject, CameraActionHandler {

    private let controller = IosCameraController.init()

    func start() {
        controller.start()
    }

    func stop() {
        controller.stop()
    }

    func takePhoto() {
        controller.takePhoto()
    }

    func onPhotoCaptured(callback: @escaping (UIImage) -> Void) {
        controller.onPhotoCaptured = callback
    }
    
    func getPreviewView() -> UIView {
        let view = CameraPreviewView()
        view.setSession(controller.session)
        return view
    }
    
    func cameraIsReady(ready: @escaping (KotlinBoolean) -> Void) {
        controller.onReadyChanged = { isReady in
            ready(KotlinBoolean(bool: isReady))
        }
    }
}
