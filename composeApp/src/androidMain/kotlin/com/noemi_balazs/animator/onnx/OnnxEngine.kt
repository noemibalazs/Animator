package com.noemi_balazs.animator.onnx

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.noemi_balazs.animator.model.CartoonType
import com.noemi_balazs.animator.model.CartoonType.BRYANDLEE
import com.noemi_balazs.animator.model.CartoonType.CELEBA
import com.noemi_balazs.animator.model.CartoonType.FACE_POINT1
import com.noemi_balazs.animator.model.CartoonType.FACE_POINT2
import com.noemi_balazs.animator.model.CartoonType.PAPRIKA
import java.io.File
import java.nio.FloatBuffer
import androidx.core.graphics.set

object OnnxEngine {

    private val environment: OrtEnvironment by lazy {
        OrtEnvironment.getEnvironment()
    }

    private var session: OrtSession? = null

    fun createSession(context: Context, type: CartoonType) {
        val modelFile = getModelFile(context, type)
        session = environment.createSession(
            modelFile.absolutePath,
            OrtSession.SessionOptions()
        )
        readSessionInfo()
    }

    private fun getModelFile(context: Context, type: CartoonType): File {
        val model = getModel(type)
        val modelFile = File(context.filesDir, model)

        if (!modelFile.exists()) {
            context.assets.open(model).use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        return modelFile
    }

    private fun getModel(cartoonType: CartoonType): String =
        when (cartoonType) {
            BRYANDLEE -> "bryandlee_animegan2.onnx"
            CELEBA -> "celeba_distill.onnx"
            FACE_POINT1 -> "face_paint_v1.onnx"
            FACE_POINT2 -> "face_paint_v2.onnx"
            PAPRIKA -> "paprika.onnx"
        }

    private fun readSessionInfo() {
        session?.let { currentSession ->
            currentSession.inputInfo.forEach { (name, info) ->
                println("Animator: session input name $name, - info: $info")
            }

            currentSession.outputInfo.forEach { (name, info) ->
                println("Animator: session output name: $name, - info: $info")
            }
        }
    }

    fun animate(bitmap: Bitmap): Result<Bitmap> = runCatching {
        val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
        val resized = bitmap.scale(512, 512)
        val width = resized.width
        val height = resized.height

        val tensor = createTensor(softwareBitmap).getOrElse { error ->
            println("Animator: createTensor failed - ${error.printStackTrace()}")
            return Result.failure(error)
        }

        val sessionResult = getSessionResult(tensor).getOrElse { error ->
            println("Animator: getSessionResult failed - ${error.printStackTrace()}")
            return Result.failure(error)
        }

        convertOutputToBitmap(sessionResult, width, height)
    }

    private fun createTensor(bitmap: Bitmap): Result<OnnxTensor> = runCatching {
        val resized = bitmap.scale(512, 512)
        val width = resized.width
        val height = resized.height

        val input = FloatArray(1 * 3 * width * height)
        val pixels = IntArray(width * height)

        resized.getPixels(
            pixels, 0, width, 0, 0, width, height
        )

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

        val shape = longArrayOf(1, 3, height.toLong(), width.toLong())

        OnnxTensor.createTensor(environment, FloatBuffer.wrap(input), shape)
    }

    private fun getSessionResult(inputTensor: OnnxTensor): Result<FloatArray> {
        if (session == null) {
            return Result.failure(IllegalStateException("Session cannot be null."))
        }
        return runCatching { generateOutput(session!!, inputTensor) }
    }

    private fun generateOutput(session: OrtSession, inputTensor: OnnxTensor): FloatArray {
        return session.run(mapOf(session.inputNames.first() to inputTensor)).use { result ->
            val outputTensor = result[0] as OnnxTensor
            val info = outputTensor.info as TensorInfo

            require(info.shape.contentEquals(longArrayOf(1, 3, 512, 512))) {
                "Unexpected output shape: ${info.shape.contentToString()}"
            }

            val buffer = outputTensor.floatBuffer
            FloatArray(buffer.remaining()).also(buffer::get)
        }
    }

    private fun convertOutputToBitmap(
        output: FloatArray,
        width: Int,
        height: Int
    ): Bitmap {
        require(output.size == 3 * width * height)

        val bitmap = createBitmap(width, height)
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

                bitmap[x, y] = Color.rgb(ir, ig, ib)
            }
        }

        return bitmap
    }

    fun closeResources() {
        session?.close()
        environment.close()
    }
}