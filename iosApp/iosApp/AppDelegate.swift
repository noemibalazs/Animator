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

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {

        guard url.scheme == "animatorshare" else {
            return false
        }
        print("Animator - opened via URL")
        loadAndCleanSharedData()
        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        print("Animator - app became active.")
        loadAndCleanSharedData()
    }

    private func loadAndCleanSharedData() {
        let appGroupID = "group.AnimatorShare"

        guard let containerURL =
        FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: appGroupID)
        else {
            print("Animator - failed to access App Group")
            return
        }

        handleFile(
            at: containerURL.appendingPathComponent("sharedImage.jpg"),
            type: "Image"
        )
    }

    private func handleFile(at url: URL, type: String) {
        guard FileManager.default.fileExists(atPath: url.path) else {
            return
        }

        print("Animator - \(type) found at: \(url.path)")

        do {
            let data = try Data(contentsOf: url)
            let byteArray = data.toKotlinByteArray()

            DispatchQueue.main.async {
                SharedMediaManager.shared.handleSharedUrl(
                    bytes: byteArray,
                    error: nil
                )
            }
        } catch {
            DispatchQueue.main.async {
                SharedMediaManager.shared.handleSharedUrl(
                    bytes: KotlinByteArray(size: 0),
                    error: KotlinThrowable(message: error.localizedDescription)
                )
            }
        }

        removeFile(at: url)
    }

    private func removeFile(at url: URL) {
        do {
            try FileManager.default.removeItem(at: url)
            print("Animator - cleaned up url path:", url.lastPathComponent)
        } catch {
            print("Animator - failed to clean up url path:", error)
        }
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
