import UIKit
import Social
import UniformTypeIdentifiers
import MobileCoreServices
import Foundation
import SwiftUI
import Photos

class ShareViewController: SLComposeServiceViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        handleSharedItem()
    }

    private func handleSharedItem() {
        guard let extensionContext = extensionContext,
              let inputItem = extensionContext.inputItems.first as? NSExtensionItem,
              let provider = inputItem.attachments?.first
        else {
            extensionContext?.completeRequest(returningItems: nil, completionHandler: nil)
            return
        }

        processProvider(provider) { [weak self] in
            self?.openMainApp()
            self?.extensionContext?.completeRequest(returningItems: nil, completionHandler: nil)
        }
    }

    private func openMainApp() {
        guard let url = URL(string: "animatorshare://share") else {
            return
        }

        var responder: UIResponder? = self
        while responder != nil {
            if let application = responder as? UIApplication {
                application.open(url, options: [:], completionHandler: nil)
                break
            }
            responder = responder?.next
        }
    }

    private func processProvider(_ provider: NSItemProvider, completion: @escaping () -> Void) {
        if provider.hasItemConformingToTypeIdentifier(UTType.image.identifier) {
            provider.loadItem(forTypeIdentifier: UTType.image.identifier, options: nil) { item, _ in
                if let url = item as? URL {
                    self.sendFileToMainApp(sourceURL: url, destinationFileName: "sharedImage.jpg")
                }
                completion()
            }

        } else {
            completion()
        }
    }

    private func sendFileToMainApp(sourceURL: URL, destinationFileName: String) {
        guard let sharedURL = sharedContainerURL() else {
            return
        }
        let destinationURL = sharedURL.appendingPathComponent(destinationFileName)

        do {
            if FileManager.default.fileExists(atPath: destinationURL.path) {
                try FileManager.default.removeItem(at: destinationURL)
            }
            try FileManager.default.copyItem(at: sourceURL, to: destinationURL)
            print("Animator - File successfully written to \(destinationURL)")
        } catch {
            print("Animator - Failed to write file: \(error.localizedDescription)")
        }
    }


    private func sharedContainerURL() -> URL? {
        let appGroupID = "group.AnimatorShare"
        if let url = FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: appGroupID) {
            return url
        } else {
            print("Animator - Failed to get shared container for app group: \(appGroupID)")
            return nil
        }
    }
}
