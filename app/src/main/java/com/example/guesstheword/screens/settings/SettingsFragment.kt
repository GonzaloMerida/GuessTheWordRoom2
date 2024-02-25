package com.example.guesstheword.screens.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.guesstheword.databinding.SettingsFragmentBinding

class SettingsFragment: Fragment(){


    private lateinit var binding : SettingsFragmentBinding

    private val settingsVM : SettingsVM by viewModels<SettingsVM> {  SettingsVM.Factory }
}