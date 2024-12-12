package com.example.talevoice.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talevoice.data.IllustPrompt
import com.example.talevoice.data.TaleIllustration
import com.example.talevoice.data.TaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class IllustrationViewModel(private val repository: TaleRepository) : ViewModel() {

    private val _illustrations = MutableStateFlow<List<TaleIllustration>>(emptyList())
    val illustrations: StateFlow<List<TaleIllustration>> = _illustrations

    private val _firstImageLoaded = MutableStateFlow(false)
    val firstImageLoaded: StateFlow<Boolean> = _firstImageLoaded

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchIllustrations(requests: List<IllustPrompt>) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.createIllustrations(requests).collect { illustration ->
                // 첫 번째 이미지 로드 감지
                if (_illustrations.value.isEmpty() && !_firstImageLoaded.value) {
                    Log.d("IllustrationViewModel", "First image loaded: ${_firstImageLoaded.value}")
                    _firstImageLoaded.value = true
                }
                // 이미지 리스트에 추가
                _illustrations.value += illustration
                // 모든 요청 완료 시 로딩 상태 해제
                if (_illustrations.value.size == requests.size) {
                    Log.d("IllustrationViewModel", "All illustrations loaded")
                    Log.d("IllustrationViewModel", _illustrations.value.toString())
                    _isLoading.value = false
                }
            }
        }
    }
}