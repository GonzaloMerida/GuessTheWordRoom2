/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.guesstheword.screens.game

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.guesstheword.R
import com.example.guesstheword.databinding.GameFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    private val gameVM : GameVM by viewModels { GameVM.Factory }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate view and obtain an instance of the binding class
        binding = GameFragmentBinding.inflate(inflater,container,false)

        setListeners()


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCollectors()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        //Listeners
        binding.correctButton.setOnClickListener { gameVM.nextWord(true) }
        binding.skipButton.setOnClickListener { gameVM.nextWord(false) }
        binding.endGameButton.setOnClickListener { onEndGame() }
    }

    private fun setCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameVM.uiState.collect { gameState ->
                    binding.scoreText.text = gameState.score.toString()
                    binding.wordText.text = gameState.word?.title ?: getString(R.string.no_more_words)
                    gameState.message?.let {
                        when(it) {
                            GameVM.GameMessages.BAD_SCORE -> Snackbar.make(requireView(),getString(R.string.bad_score),Snackbar.LENGTH_SHORT).show()
                            GameVM.GameMessages.GOOD_SCORE -> Snackbar.make(requireView(),getString(R.string.good_score),Snackbar.LENGTH_SHORT).show()
                        }
                        gameVM.messageShown()
                    }
                    if(gameState.noMoreWords) {
                        binding.correctButton.isEnabled = false
                        binding.skipButton.isEnabled = false
                        Snackbar.make(requireView(),getString(R.string.no_more_words),Snackbar.LENGTH_SHORT).show()
                        gameVM.disableButtonsCompleted()
                    }
                    binding.tvTime.text = gameState.time.toInt().toString()
                }
            }
        }
    }

    private fun onEndGame() {
        val action = GameFragmentDirections.actionGameFragmentToScoreFragment(
            gameVM.uiState.value.rightWords.toTypedArray(),
            gameVM.uiState.value.wrongWords.toTypedArray()
        )
        action.score = gameVM.uiState.value.score
        findNavController().navigate(action)
    }
}
