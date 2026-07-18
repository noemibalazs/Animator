import UIKit
import AVFoundation

final class CameraPreviewView: UIView {

    private var previewLayer: AVCaptureVideoPreviewLayer?

    func setSession(_ session: AVCaptureSession) {
        let layer = AVCaptureVideoPreviewLayer(session: session)

        layer.videoGravity = .resizeAspectFill

        self.layer.addSublayer(layer)

        previewLayer = layer
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        previewLayer?.frame = bounds
    }
}
