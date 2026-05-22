package com.example.composeapp.data.local.viewmodel

import com.example.composeapp.data.local.UserEntity
import com.example.composeapp.data.local.repository.UserRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // Converts Flow → StateFlow for UI
    val users: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addUser(name: String, email: String, age: Int) {
        viewModelScope.launch {
            repository.addUser(
                UserEntity(name = name, email = email, age = age)
            )
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    // Factory needed because ViewModel has constructor params
    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
    }
}