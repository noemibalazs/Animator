package com.noemi_balazs.animator.feature.animator

import com.noemi_balazs.animator.base.BaseViewModel
import com.noemi_balazs.animator.common.Cartoonizer
import com.noemi_balazs.animator.common.ToastManager
import com.noemi_balazs.animator.data.datastore.AppDataStore
import com.noemi_balazs.animator.data.repository.AnimatorRepository
import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.di.getBytes
import com.noemi_balazs.animator.model.AnimatedCartoon
import com.noemi_balazs.animator.model.CartoonType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class AnimatorViewModel(
    private val dataStore: AppDataStore,
    private val cartoonizer: Cartoonizer,
    private val animatorRepository: AnimatorRepository,
    private val toastManager: ToastManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AnimatorUIState())
    val uiState = _uiState.asStateFlow()

    fun clearImage() = runCoroutine {
        dataStore.saveImage("")
    }

    fun clearResources() = cartoonizer.cleaResources()

    fun cartoonizeImage(type: CartoonType) = runCoroutine {
        val image = _uiState.value.defaultImage ?: return@runCoroutine

        withContext(Dispatchers.IO) {
            withProgress {
                with(cartoonizer) {
                    createSession(type)
                    cartoonize(image)
                        .onSuccess { animatedImage ->
                            _uiState.update {
                                it.copy(
                                    currentType = type,
                                    animatedImage = animatedImage,
                                    showAnimatedImage = true,
                                    showDefaultImage = false
                                )
                            }
                        }
                        .onFailure { error ->
                            when (error) {
                                is CancellationException -> throw error
                                is Error -> throw error
                                else -> toastManager.showMessage(
                                    error.message ?: "Something went wrong, try it again!"
                                )
                            }
                        }
                }
            }
        }
    }

    fun updateCurrentImage(image: AnimatorImage) {
        _uiState.update {
            it.copy(
                defaultImage = image,
                showDefaultImage = true
            )
        }
    }

    fun deleteCartoonizedImage() = runCoroutine {
        _uiState.update {
            it.copy(
                showDefaultImage = true,
                showAnimatedImage = false,
                animatedImage = null
            )
        }

        clearResources()
    }

    fun saveAnimatedImage(onSuccess: () -> Unit) = runCoroutine {
        val uiState = _uiState.value
        val type = uiState.currentType
        val animatedImage = uiState.animatedImage

        if (type == null || animatedImage == null) return@runCoroutine

        withProgress {
            val timeStamp = Clock.System.now().toEpochMilliseconds()

            animatedImage.getBytes()?.let { bytes ->
                animatorRepository.saveCartoon(
                    AnimatedCartoon(
                        id = timeStamp,
                        type = type,
                        imageData = bytes
                    )
                )

                clearResources()
                onSuccess()
            }
        }
    }

    override fun onCleared() {
        clearImage()
        clearResources()
        super.onCleared()
    }
}