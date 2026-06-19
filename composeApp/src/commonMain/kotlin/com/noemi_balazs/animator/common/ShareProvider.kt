package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

interface ShareProvider {

    fun share(bytes: ByteArray)
}

@Composable
expect fun provideShareProvider(): ShareProvider

