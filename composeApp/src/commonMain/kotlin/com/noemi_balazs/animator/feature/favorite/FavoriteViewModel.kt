package com.noemi_balazs.animator.feature.favorite

import com.noemi_balazs.animator.base.BaseViewModel
import com.noemi_balazs.animator.data.repository.AnimatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoriteViewModel(
    private val animatorRepository: AnimatorRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUIState())
    val uiState = _uiState.asStateFlow()

    init {
        observeCartoons()
    }

    private fun observeCartoons() = runCoroutine {
        animatorRepository.observeCartoons().collectFlow { cartoons ->
            val sortedCartoons = cartoons.sortedByDescending { it.id }
            _uiState.update {
                it.copy(cartoons = sortedCartoons)
            }
        }
    }
}