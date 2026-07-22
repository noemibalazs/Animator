package com.noemi_balazs.animator.di

import android.graphics.Bitmap
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.noemi_balazs.animator.common.AndroidCartoonizer
import com.noemi_balazs.animator.common.AndroidCamera
import com.noemi_balazs.animator.common.AndroidGallery
import com.noemi_balazs.animator.common.AndroidImageProvider
import com.noemi_balazs.animator.common.AndroidPermissionHandler
import com.noemi_balazs.animator.common.AndroidPlatformFileStorage
import com.noemi_balazs.animator.common.AndroidShareProvider
import com.noemi_balazs.animator.common.AndroidToastManager
import com.noemi_balazs.animator.common.Cartoonizer
import com.noemi_balazs.animator.common.Camera
import com.noemi_balazs.animator.common.Gallery
import com.noemi_balazs.animator.common.ImageProvider
import com.noemi_balazs.animator.common.PermissionHandler
import com.noemi_balazs.animator.common.PlatformFileStorage
import com.noemi_balazs.animator.common.ShareProvider
import com.noemi_balazs.animator.common.ToastManager
import com.noemi_balazs.animator.common.createDataStore
import com.noemi_balazs.animator.common.getDatabase
import com.noemi_balazs.animator.data.database.AnimatorDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.ByteArrayOutputStream

actual typealias AnimatorImage = Bitmap

actual typealias AnimatorView = View

actual fun AnimatorImage.getBytes(): ByteArray? {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

actual val platformModule: Module = module {

    single<PermissionHandler> { AndroidPermissionHandler() }
    single<ImageProvider> { AndroidImageProvider() }
    single<ShareProvider> { AndroidShareProvider(androidContext()) }
    single<ToastManager> { AndroidToastManager(androidContext()) }

    single<Camera> { AndroidCamera() }
    single<Gallery> { AndroidGallery() }
    single<Cartoonizer> { AndroidCartoonizer(androidContext()) }

    single<DataStore<Preferences>> { createDataStore(androidContext()) }
    single<AnimatorDataBase> { getDatabase(androidContext()) }
    single<PlatformFileStorage> { AndroidPlatformFileStorage(androidContext()) }
}