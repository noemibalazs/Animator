package com.noemi_balazs.animator.onnx

import com.noemi_balazs.animator.model.CartoonType
import com.noemi_balazs.animator.model.CartoonType.BRYANDLEE
import com.noemi_balazs.animator.model.CartoonType.CELEBA
import com.noemi_balazs.animator.model.CartoonType.FACE_POINT1
import com.noemi_balazs.animator.model.CartoonType.FACE_POINT2
import com.noemi_balazs.animator.model.CartoonType.PAPRIKA
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import onnxruntime.animator_ort_create_env
import onnxruntime.animator_ort_create_float_tensor
import onnxruntime.animator_ort_create_session
import onnxruntime.animator_ort_get_api
import onnxruntime.animator_ort_get_error_message
import onnxruntime.animator_ort_release_env
import onnxruntime.animator_ort_release_session
import onnxruntime.animator_ort_release_status
import onnxruntime.animator_ort_release_value
import onnxruntime.animator_ort_run
import onnxruntime.animator_ort_session_input_name
import onnxruntime.animator_ort_session_output_name
import onnxruntime.animator_ort_tensor_floats
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSBundle
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation

@OptIn(ExperimentalForeignApi::class)
object OnnxEngine {

    private const val INPUT_SIZE = 512
    private const val NAME_BUF_LEN = 256

    private val api: COpaquePointer =
        animator_ort_get_api() ?: error("Animator - failed to get OrtApi")

    private var environment: COpaquePointer? = null
    private var session: COpaquePointer? = null
    private var inputName: String? = null
    private var outputName: String? = null

    fun createSession(type: CartoonType) {
        ensureEnv()

        session?.let {
            animator_ort_release_session(api, it)
            session = null
            inputName = null
            outputName = null
        }

        val modelPath = getModelPath(getModelName(type))
        memScoped {
            val sessionOut = alloc<COpaquePointerVar>()
            val status = animator_ort_create_session(
                api,
                environment,
                modelPath,
                sessionOut.ptr
            )
            checkStatus(status)
            session = sessionOut.value
        }

        readSessionInfo()
    }

    fun animate(image: UIImage): Result<UIImage> = runCatching {
        val resized = image.resized(INPUT_SIZE, INPUT_SIZE)
        val width = INPUT_SIZE
        val height = INPUT_SIZE

        val input = createInputFloats(resized, width, height)
        val output = runInference(input)
        convertOutputToUIImage(output, width, height)
    }

    fun closeResources() {
        session?.let { animator_ort_release_session(api, it) }
        session = null
        inputName = null
        outputName = null

        environment?.let { animator_ort_release_env(api, it) }
        environment = null
    }

    private fun ensureEnv() {
        if (environment != null) return
        memScoped {
            val envOut = alloc<COpaquePointerVar>()
            val status = animator_ort_create_env(api, envOut.ptr)
            checkStatus(status)
            environment = envOut.value
        }
    }

    private fun readSessionInfo() {
        val currentSession = session ?: return
        memScoped {
            val nameBuf = allocArray<ByteVar>(NAME_BUF_LEN)

            checkStatus(
                animator_ort_session_input_name(
                    api,
                    currentSession,
                    0uL,
                    nameBuf,
                    NAME_BUF_LEN.toULong()
                )
            )
            inputName = nameBuf.toKString()
            println("Animator: session input name $inputName")

            checkStatus(
                animator_ort_session_output_name(
                    api,
                    currentSession,
                    0uL,
                    nameBuf,
                    NAME_BUF_LEN.toULong()
                )
            )
            outputName = nameBuf.toKString()
            println("Animator: session output name: $outputName")
        }
    }

    private fun createInputFloats(image: UIImage, width: Int, height: Int): FloatArray {
        val pixels = image.rgbaPixels(width, height)
        val input = FloatArray(1 * 3 * width * height)

        val rOffset = 0
        val gOffset = width * height
        val bOffset = 2 * gOffset

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]

                var r = ((pixel shr 16) and 0xFF) / 255f
                var g = ((pixel shr 8) and 0xFF) / 255f
                var b = (pixel and 0xFF) / 255f

                r = r * 2f - 1f
                g = g * 2f - 1f
                b = b * 2f - 1f

                val index = y * width + x
                input[rOffset + index] = r
                input[gOffset + index] = g
                input[bOffset + index] = b
            }
        }

        return input
    }

    private fun runInference(input: FloatArray): FloatArray {
        val currentSession = session
            ?: throw IllegalStateException("Animator - session cannot be null.")
        val inName = inputName
            ?: throw IllegalStateException("Animator - input name cannot be null.")
        val outName = outputName
            ?: throw IllegalStateException("Animator - output name cannot be null.")

        val expectedCount = 1 * 3 * INPUT_SIZE * INPUT_SIZE
        require(input.size == expectedCount) {
            "Animator - unexpected input size: ${input.size}"
        }

        return input.usePinned { inputPinned ->
            memScoped {
                val shape = allocArray<LongVar>(4)
                shape[0] = 1L
                shape[1] = 3L
                shape[2] = INPUT_SIZE.toLong()
                shape[3] = INPUT_SIZE.toLong()

                val tensorOut = alloc<COpaquePointerVar>()
                checkStatus(
                    animator_ort_create_float_tensor(
                        api,
                        inputPinned.addressOf(0),
                        input.size.toULong(),
                        shape,
                        4uL,
                        tensorOut.ptr
                    )
                )
                val inputTensor = tensorOut.value
                    ?: error("Animator - failed to create input tensor")

                try {
                    val outputOut = alloc<COpaquePointerVar>()
                    checkStatus(
                        animator_ort_run(
                            api,
                            currentSession,
                            inName,
                            inputTensor,
                            outName,
                            outputOut.ptr
                        )
                    )
                    val outputTensor = outputOut.value
                        ?: error("Animator - failed to get output tensor")

                    try {
                        val output = FloatArray(expectedCount)
                        output.usePinned { outputPinned ->
                            checkStatus(
                                animator_ort_tensor_floats(
                                    api,
                                    outputTensor,
                                    outputPinned.addressOf(0),
                                    expectedCount.toULong()
                                )
                            )
                        }
                        output
                    } finally {
                        animator_ort_release_value(api, outputTensor)
                    }
                } finally {
                    animator_ort_release_value(api, inputTensor)
                }
            }
        }
    }

    private fun convertOutputToUIImage(
        output: FloatArray,
        width: Int,
        height: Int
    ): UIImage {
        require(output.size == 3 * width * height)

        val bytesPerPixel = 4
        val bytesPerRow = width * bytesPerPixel
        val raw = ByteArray(height * bytesPerRow)
        val channelSize = width * height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelIndex = y * width + x

                var r = output[pixelIndex]
                var g = output[pixelIndex + channelSize]
                var b = output[2 * channelSize + pixelIndex]

                r = (r + 1f) * 0.5f
                g = (g + 1f) * 0.5f
                b = (b + 1f) * 0.5f

                val ir = (r * 255f).toInt().coerceIn(0, 255)
                val ig = (g * 255f).toInt().coerceIn(0, 255)
                val ib = (b * 255f).toInt().coerceIn(0, 255)

                val offset = pixelIndex * bytesPerPixel
                raw[offset] = ir.toByte()
                raw[offset + 1] = ig.toByte()
                raw[offset + 2] = ib.toByte()
                raw[offset + 3] = 0xFF.toByte()
            }
        }

        return raw.usePinned { pinned ->
            val colorSpace = CGColorSpaceCreateDeviceRGB()
                ?: error("Animator - failed to create color space")
            val context = CGBitmapContextCreate(
                data = pinned.addressOf(0),
                width = width.toULong(),
                height = height.toULong(),
                bitsPerComponent = 8u,
                bytesPerRow = bytesPerRow.toULong(),
                space = colorSpace,
                bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
            ) ?: error("Animator - failed to create bitmap context")

            val cgImage = CGBitmapContextCreateImage(context)
                ?: error("Animator - failed to create CGImage")
            CGContextRelease(context)
            CGColorSpaceRelease(colorSpace)
            UIImage(
                cGImage = cgImage,
                scale = 1.0,
                orientation = UIImageOrientation.UIImageOrientationUp
            )
        }
    }

    private fun UIImage.resized(width: Int, height: Int): UIImage {
        val size = CGSizeMake(width.toDouble(), height.toDouble())
        UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
        drawInRect(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))
        val result = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return result ?: this
    }

    private fun UIImage.rgbaPixels(width: Int, height: Int): IntArray {
        val cgImage = CGImage ?: error("Animator - UIImage has no CGImage")
        val bytesPerPixel = 4
        val bytesPerRow = bytesPerPixel * width
        val raw = ByteArray(bytesPerRow * height)

        raw.usePinned { pinned ->
            val colorSpace = CGColorSpaceCreateDeviceRGB()
                ?: error("Animator - failed to create color space")
            val context = CGBitmapContextCreate(
                data = pinned.addressOf(0),
                width = width.toULong(),
                height = height.toULong(),
                bitsPerComponent = 8u,
                bytesPerRow = bytesPerRow.toULong(),
                space = colorSpace,
                bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
            ) ?: error("Animator - failed to create bitmap context")

            CGContextDrawImage(
                context,
                CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                cgImage
            )
            CGContextRelease(context)
            CGColorSpaceRelease(colorSpace)
        }

        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val offset = i * bytesPerPixel
            val r = raw[offset].toInt() and 0xFF
            val g = raw[offset + 1].toInt() and 0xFF
            val b = raw[offset + 2].toInt() and 0xFF
            val a = raw[offset + 3].toInt() and 0xFF
            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        return pixels
    }

    private fun getModelPath(modelName: String): String {
        return requireNotNull(
            NSBundle.mainBundle.pathForResource(
                name = modelName,
                ofType = "onnx"
            )
        ) {
            "Animator - Model $modelName.onnx not found in app bundle."
        }
    }

    private fun getModelName(cartoonType: CartoonType): String =
        when (cartoonType) {
            BRYANDLEE -> "bryandlee_animegan2"
            CELEBA -> "celeba_distill"
            FACE_POINT1 -> "face_paint_v1"
            FACE_POINT2 -> "face_paint_v2"
            PAPRIKA -> "paprika"
        }

    private fun checkStatus(status: COpaquePointer?) {
        if (status == null) return
        val message = animator_ort_get_error_message(api, status)?.toKString()
        animator_ort_release_status(api, status)
        error("Animator - ORT error: $message")
    }
}
