package com.example.guesstheword.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.guesstheword.datamodel.UserPreferences
import com.example.guesstheword.dependencies.MyApplication
import com.example.guesstheword.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsVM(
    private val userPrefsRepository : UserPreferencesRepository
) : ViewModel() {

    private val _uiState : MutableStateFlow<UserPreferences> = MutableStateFlow(UserPreferences())
    val uiState : StateFlow<UserPreferences> = _uiState.asStateFlow()

    init {
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return SettingsVM(
                    userPrefsRepository = (application as MyApplication).appcontainer.
                ) as T
            }
        }
    }
}