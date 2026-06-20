import Foundation
import ComposeApp
import PhotosUI

class IosImagePickerBridge: NSObject, ImagePicker {
    
    private var continuation: CheckedContinuation<String?, Never>?

    func pickImage() async -> String? {
        return await withCheckedContinuation { continuation in
            self.continuation = continuation

            Task { @MainActor in
                var config = PHPickerConfiguration()
                config.filter = .images

                let picker = PHPickerViewController(configuration: config)
                picker.delegate = self
                
                topViewController()?.present(picker, animated: true)
            }
        }
    }
}

extension IosImagePickerBridge: PHPickerViewControllerDelegate {
    
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let item = results.first?.itemProvider else {
            continuation?.resume(returning: nil)
            return
        }
        
        item.loadFileRepresentation(forTypeIdentifier: "public.image") { url, error in
            guard let url else {
                self.continuation?.resume(returning: nil)
                return
            }

            let cacheDir = FileManager.default.urls(
                for: .cachesDirectory,
                in: .userDomainMask
            ).first!

            let destination = cacheDir.appendingPathComponent(
                UUID().uuidString + ".jpg"
            )

            do {
                try FileManager.default.copyItem(
                    at: url,
                    to: destination
                )

                self.continuation?.resume(
                    returning: destination.absoluteString
                )
            } catch {
                self.continuation?.resume(returning: nil)
            }
        }
    }
}

private func topViewController() -> UIViewController? {
    guard let scene = UIApplication.shared.connectedScenes
        .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
          let window = scene.windows.first(where: { $0.isKeyWindow }),
          let root = window.rootViewController
    else {
        return nil
    }

    var top = root
    while let presented = top.presentedViewController {
        top = presented
    }
    return top
}
