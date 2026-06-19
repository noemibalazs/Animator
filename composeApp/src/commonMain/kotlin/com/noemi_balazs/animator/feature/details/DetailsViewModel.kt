package com.noemi_balazs.animator.feature.details

import com.noemi_balazs.animator.base.BaseViewModel
import com.noemi_balazs.animator.data.repository.AnimatorRepository
import com.noemi_balazs.animator.model.AnimatedCartoon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DetailsViewModel(
    private val animatorRepository: AnimatorRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun loadAnimatedCartoon(id: Long) = runCoroutine {
        val image = animatorRepository.getImage(id)
        _uiState.update {
            it.copy(cartoon = image)
        }
    }

    fun deleteCartoon(cartoon: AnimatedCartoon, onSuccess: () -> Unit) = runCoroutine {
        runCatching {
            animatorRepository.deleteCartoon(cartoon)
        }
            .onSuccess {
                onSuccess()
            }
    }
}