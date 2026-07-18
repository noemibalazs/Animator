import Foundation
import UIKit
import AVFoundation

final class IosCameraController: NSObject {

    let session = AVCaptureSession()
    private let photoOutput = AVCapturePhotoOutput()
    private var videoInput: AVCaptureDeviceInput?
    private let sessionQueue = DispatchQueue(label: "camera.session.queue")
    
    private(set) var isReady = false {
       didSet {
           sessionQueue.async {
               self.onReadyChanged?(self.isReady)
           }
       }
    }

    var onPhotoCaptured: ((UIImage) -> Void)?
    var onReadyChanged: ((Bool) -> Void)?

    override init() {
        super.init()
        configure()
    }
}

extension IosCameraController {

    func configure() {
        session.beginConfiguration()
        session.sessionPreset = .photo

        guard let device = AVCaptureDevice.default(
            .builtInWideAngleCamera,
            for: .video,
            position: .back
        ) else { return }

        guard let input = try? AVCaptureDeviceInput(device: device),
              session.canAddInput(input)
        else { return }

        session.addInput(input)
        videoInput = input

        if session.canAddOutput(photoOutput) {
            session.addOutput(photoOutput)
        }

        session.commitConfiguration()
    }

    func start() {
        sessionQueue.async {
            if !self.session.isRunning {
                self.session.startRunning()
            }

            self.isReady = true
        }
    }

    func stop() {
        if session.isRunning {
            session.stopRunning()
        }
    }


    func takePhoto() {
        sessionQueue.async {
            guard self.isReady else {
                return
            }

            let settings = AVCapturePhotoSettings()

            self.photoOutput.capturePhoto(
                with: settings,
                delegate: self
            )
        }
    }
}

extension IosCameraController: AVCapturePhotoCaptureDelegate {

    func photoOutput(
        _ output: AVCapturePhotoOutput,
        didFinishProcessingPhoto photo: AVCapturePhoto,
        error: Error?
    ) {
        guard let data = photo.fileDataRepresentation(),
              let image = UIImage(data: data)
        else {
            return
        }
        onPhotoCaptured?(image)
    }
}
