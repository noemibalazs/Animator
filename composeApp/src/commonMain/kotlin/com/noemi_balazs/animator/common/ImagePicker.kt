package com.noemi_balazs.animator.common

interface ImagePicker {

    suspend fun pickImage(): String?
}