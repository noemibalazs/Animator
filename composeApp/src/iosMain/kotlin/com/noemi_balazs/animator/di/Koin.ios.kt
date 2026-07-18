package com.noemi_balazs.animator.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.noemi_balazs.animator.common.Cartoonizer
import com.noemi_balazs.animator.common.IosCartoonizer
import com.noemi_balazs.animator.common.IosCamera
import com.noemi_balazs.animator.common.Camera
import com.noemi_balazs.animator.common.Gallery
import com.noemi_balazs.animator.common.ImageProvider
import com.noemi_balazs.animator.common.IosGallery
import com.noemi_balazs.animator.common.IosImageProvider
import com.noemi_balazs.animator.common.IosPermissionHandler
import com.noemi_balazs.animator.common.IosShareProvider
import com.noemi_balazs.animator.common.IosToastManager
import com.noemi_balazs.animator.common.PermissionHandler
import com.noemi_balazs.animator.common.ShareProvider
import com.noemi_balazs.animator.common.ToastManager
import com.noemi_balazs.animator.common.createDataStore
import com.noemi_balazs.animator.common.getDatabase
import com.noemi_balazs.animator.data.database.AnimatorDataBase
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIView
import platform.posix.memcpy

actual typealias AnimatorImage = UIImage

actual typealias AnimatorView = UIView

actual fun AnimatorImage.getBytes(): ByteArray? {
    val data = UIImagePNGRepresentation(this) ?: return null

    return ByteArray(data.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(
                pinned.addressOf(0),
                data.bytes,
                data.length
            )
        }
    }
}

actual val platformModule: Module = module {

    single<PermissionHandler> { IosPermissionHandler() }
    single<ImageProvider> { IosImageProvider() }
    single<ShareProvider> { IosShareProvider() }
    single<ToastManager> { IosToastManager() }

    single<Camera> { IosCamera(get()) }
    single<Gallery> { IosGallery(get()) }
    single<Cartoonizer> { IosCartoonizer() }

    single<DataStore<Preferences>> { createDataStore() }
    single<AnimatorDataBase> { getDatabase() }
}


