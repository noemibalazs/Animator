import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }

        val onnxXcframework = rootProject.file("frameworks/onnxruntime.xcframework")

        val onnxCinteropHeaders =
            rootProject.file("frameworks/onnxruntime.xcframework/ios-arm64/onnxruntime.framework/Headers")

        val onnxFrameworkSearchDir = when (iosTarget.name) {
            "iosArm64" -> onnxXcframework.resolve("ios-arm64")
            "iosSimulatorArm64" -> onnxXcframework.resolve("ios-arm64_x86_64-simulator")
            else -> error("Unexpected iOS Kotlin target name: ${iosTarget.name}")
        }

        iosTarget.binaries.all {
            linkerOpts(
                "-F${onnxFrameworkSearchDir.absolutePath}",
                "-framework", "onnxruntime",
            )
        }

        iosTarget.compilations["main"].cinterops {
            val onnxruntime by creating {
                defFile(project.file("src/nativeInterop/cinterop/onnx.def"))

                packageName("onnxruntime")
                includeDirs(
                    project.file("src/nativeInterop/cinterop"),
                    onnxCinteropHeaders,
                )
            }
        }
    }

    cocoapods {
        summary = "Animator"
        homepage = "."
        version = "1.0.0"
        ios.deploymentTarget = "16.0"

        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ComposeApp"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.process)

            implementation(libs.microsoft.onnx)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.camerax.core)
            implementation(libs.camerax.camera2)
            implementation(libs.camerax.view)
            implementation(libs.camerax.lifecycle)
            implementation(libs.camerax.extension)
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.backhandler)

            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)

            implementation(libs.navigation.compose)
            implementation(libs.kotlin.coroutine)
            implementation(libs.kotlin.date.time)
            implementation(libs.coil.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.okio.hashing)
        }
    }
}

android {
    namespace = "com.noemi_balazs.animator"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.noemi_balazs.animator"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
    }

    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)

        add("kspAndroid", libs.room.compiler)
        add("kspIosArm64", libs.room.compiler)
        add("kspIosSimulatorArm64", libs.room.compiler)
    }
}

compose.resources {
    publicResClass = false
    generateResClass = auto
    packageOfResClass = "com.noemi_balazs.animator.resources"
}

room {
    schemaDirectory("$projectDir/schemas")
}

