package com.example.guesstheword.screens.game

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.guesstheword.datamodel.Game
import com.example.guesstheword.datamodel.Word
import com.example.guesstheword.dependencies.MyApplication
import com.example.guesstheword.repositories.GamesRepository
import com.example.guesstheword.repositories.WordsRepository
import com.example.guesstheword.utils.MyTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.floor

class GameVM(
    val wordsRepository: WordsRepository
    ) : ViewModel() {

    //Flujo de estado.
    private val _uiState : MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState())
    val uiState : StateFlow<GameUiState> = _uiState.asStateFlow()


    init {
        //inicializa las palabras.
        viewModelScope.launch {
            //numero de palabras que toma
            val words = wordsRepository.getSomeRandomWords(NUM_WORDS)
            if(words.size > 0) { //si hay más de una palabra.
                _uiState.update { currentSate ->
                    currentSate.copy(
                        word = words[0],
                        score = 0,
                        wordList = if (words.size > 1) words.subList(1,words.size) else emptyList()
                    )
                }
            } else { //no tiene palabras, levanta bandera para desactivar botones.
                _uiState.update { currentSate ->
                    currentSate.copy(
                        word = null,
                        wordList = emptyList(),
                        noMoreWords = true,
                        message = GameMessages.GOOD_SCORE
                    )
                }
            }
        }
        //inicializa el temporizador.
        viewModelScope.launch {
            MyTimer(10f,0.5f,500).timer.filter {
                it - floor(it) == 0f
            }.collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        time = it
                    )
                }
            }
        }
    }

    //Función que toma una nueva palabra e incrementa o decrementa la puntuación.
    fun nextWord(acierto : Boolean) {
        val inc = if (acierto) 1 else -1
        //si palabra no es nula
        uiState.value.word?.let {
            _uiState.update { currentState ->
                currentState.copy(
                    //si la lista está vacia la próxima palabra es nula.
                    word = if (currentState.wordList.isEmpty()) null else currentState.wordList[0],
                    score = currentState.score+inc,
                    wordList = if(currentState.wordList.size > 1) currentState.wordList.subList(1,currentState.wordList.size) else emptyList(),
                    rightWords = if (acierto)
                        currentState.rightWords.plus(currentState.word!!.title)
                    else
                        currentState.rightWords,
                    wrongWords = if (!acierto)
                        currentState.wrongWords.plus(currentState.word!!.title)
                    else
                        currentState.wrongWords,
                    //si la lista vacía, no hay mas palabras (flag).
                    noMoreWords = currentState.wordList.isEmpty()
                )
            }

        }
        //mostrar mensaje por pantalla cuando la puntuación llegue a 10
        if(uiState.value.score == GOOD_SCORE && inc == 1)
            _uiState.update { currenState ->
                currenState.copy(
                    message = GameMessages.GOOD_SCORE
                )
            }
        if(uiState.value.score == BAD_SCORE && inc == -1)
            _uiState.update { currenState ->
                currenState.copy(
                    message = GameMessages.BAD_SCORE
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destruido!")
    }

    fun messageShown() {
        _uiState.update { currenState ->
            currenState.copy(
                message = null
            )
        }
    }

    fun disableButtonsCompleted() {
        _uiState.update {
            it.copy(
                noMoreWords = false
            )
        }
    }


    companion object {
        //número máximo de palabras del juego.
        val NUM_WORDS = 10

        //Valor de buena puntuación.
        val GOOD_SCORE = 6
        val BAD_SCORE = -1

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return GameVM(
                    (application as MyApplication).appcontainer.wordsRepository
                ) as T
            }
        }
    }

    enum class GameMessages {
        BAD_SCORE, GOOD_SCORE
    }
}