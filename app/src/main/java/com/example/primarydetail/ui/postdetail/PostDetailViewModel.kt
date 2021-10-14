package com.example.primarydetail.ui.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primarydetail.model.Post
import com.example.primarydetail.ui.PostRepository
import com.example.primarydetail.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiState(
    val post: Post? = null,
    val loading: Boolean = false
) {
    /**
     * True if the post couldn't be found
     */
    val failedLoading: Boolean
        get() = !loading && post == null
}

@HiltViewModel
class PostDetailViewModel @Inject constructor(private val repository: PostRepository) :
    ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(PostDetailUiState(loading = true))
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun retrievePost(postId: Long) = viewModelScope.launch {
        val postResult = repository.postById(postId)
        _uiState.update {
            when (postResult) {
                is Result.Success -> it.copy(post = postResult.data, loading = false)
                else -> it.copy(loading = false)
            }
        }
    }

    /**
     * Mark a post as read via repository
     * @param postId The ID of the post to mark as read
     */
    fun markRead(postId: Long) = viewModelScope.launch {
        repository.markRead(postId)
    }

    /**
     * Delete a post via repository
     * @param postId The ID of the post to delete
     */
    fun deletePost(postId: Long) = viewModelScope.launch {
        repository.deletePost(postId)
    }
}