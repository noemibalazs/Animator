import Foundation
import ComposeApp

class IosCameraHandlerBridge: CameraHandler {

    private let manager: NotificationManager

    init(manager: NotificationManager) {
        self.manager = manager
    }

    func requestPermission() {
        manager.requestCameraPermission()
    }

    func openSettings() {
        manager.openSettings()
    }
    
    func refreshState(){
        manager.refreshCameraState()
    }
}

