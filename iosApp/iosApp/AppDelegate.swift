import Foundation
import ComposeApp
import UIKit
import SwiftUI
import AVFoundation

class AppDelegate: NSObject, UIApplicationDelegate {

    private lazy var manager = NotificationManager()
    private lazy var cameraBridge = IosCameraHandlerBridge(manager: manager)

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {

        CameraHandlerBridge.shared.handler = cameraBridge
        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        print("Animator - app became active.")
    }
}

enum CameraState: String {
    case granted
    case notDetermined
    case denied
    case restricted
}

struct CameraUIState {
    let state: CameraState
}

final class NotificationManager {

    func requestCameraPermission() {
        AVCaptureDevice.requestAccess(for: .video) { granted in
            let uiState: CameraUIState
            if granted {
                uiState = CameraUIState(state: .granted)
            } else {
                uiState = CameraUIState(state: .notDetermined)
            }

            DispatchQueue.main.async {
                CameraStateHandler.shared.setCameraState(state: uiState.state.rawValue)
            }
        }
    }

    func refreshCameraState() {
        checkCameraState { uiState in
            CameraStateHandler.shared.setCameraState(state: uiState.state.rawValue)
        }
    }

    private func checkCameraState(completion: @escaping (CameraUIState) -> Void) {
        let uiState: CameraUIState

        switch AVCaptureDevice.authorizationStatus(for: .video) {

        case .authorized:
            uiState = CameraUIState(state: .granted)
        case .notDetermined:
            uiState = CameraUIState(state: .notDetermined)
        case .denied:
            uiState = CameraUIState(state: .denied)
        case .restricted:
            uiState = CameraUIState(state: .restricted)
        @unknown default:
            uiState = CameraUIState(state: .denied)
        }

        DispatchQueue.main.async {
            completion(uiState)
        }
    }

    func openSettings() {
        guard let url = URL(string: UIApplication.openSettingsURLString) else {
            return
        }

        UIApplication.shared.open(url)
    }
}
