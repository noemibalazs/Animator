# Animator

A Kotlin Multiplatform mobile app that turns photos into cartoon-style images using on-device ONNX models.

Built with **Compose Multiplatform**, **Koin**, **Room**, and **ONNX Runtime** for Android and iOS.

## Features
* Choose a photo from your gallery, share one directly with the app via the iOS Share Extension or Android Intent Filter, or capture a new photo with your camera.
* Transform your photos with five unique cartoon styles:
    * Bryandlee
    * Celeba
    * Face Paint v1
    * Face Paint v2
    * Paprika
* Save your favorite creations and browse your animation history.
* Easily share your transformed photos with others.

## Tech stack
- Kotlin Multiplatform
- Compose Multiplatform
- ONNX Runtime (Android SDK + iOS XCFramework)
- Room
- Koin
- CameraX (Android) / AVFoundation (iOS)

## ONNX model setup
Model files are intentionally not committed to Git (see .gitignore). Add the required model files locally before running inference, 
or provide your own models and update the engine configuration accordingly.

### Required files
| File                       | Used by style       |
|----------------------------|---------------------|
| `bryandlee_animegan2.onnx` | Bryandlee           |
| `celeba_distill.onnx`      | Celeba              |
| `face_paint_v1.onnx`       | Face Paint v1       |
| `face_paint_v2.onnx`       | Face Paint v2       |
| `paprika.onnx`             | Paprika             |

### Android
Place the models in:
composeApp/src/androidMain/assets/

### iOS
Place the models in:
iosApp/Resources/

Make sure the files are included in the Xcode app bundle (add them to the iOS target if needed).
> **Note:** If `iosApp/Resources/` does not exist yet, create it first:
> mkdir -p iosApp/Resources


## ONNX inference
- **Android:** uses the official ONNX Runtime Android library
- **iOS:** uses a C API bridge (`onnx_bridge.h`) over the bundled XCFramework, because opaque ORT types are not directly usable from Kotlin/Native cinterop

Both platforms resize input to **512×512**, normalize pixels to `[-1, 1]`, run inference, and convert the output back to an image.

## Troubleshooting
### Models not found (iOS)
If you see an error like `Model *.onnx not found in app bundle`, check that:
1. Files exist in `iosApp/Resources/`
2. They are included in the Xcode target’s **Copy Bundle Resources**

### Models not found (Android)
Ensure files are under `composeApp/src/androidMain/assets/` with the exact filenames listed above.

### iOS build / cinterop issues
Regenerate ONNX cinterop bindings:

./gradlew :composeApp:cinteropOnnxruntimeIosArm64 :composeApp:cinteropOnnxruntimeIosSimulatorArm64
./gradlew :composeApp:commonizeCInterop


## Screenshots

![Caleba before.png](../../Desktop/Caleba%20before.png)

![Caleba after.png](../../Desktop/Caleba%20after.png)

![Bryandlee before.JPG](../../Desktop/Bryandlee%20before.JPG)

![Bryandlee after.PNG](../../Desktop/Bryandlee%20after.PNG)