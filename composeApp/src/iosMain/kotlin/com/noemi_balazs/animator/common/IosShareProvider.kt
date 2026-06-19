package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

class IosShareProvider : ShareProvider {

    override fun share(bytes: ByteArray) {

    }
}

@Composable
actual fun provideShareProvider(): ShareProvider = IosShareProvider()