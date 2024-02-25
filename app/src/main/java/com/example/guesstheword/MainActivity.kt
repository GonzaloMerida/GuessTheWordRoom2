package com.example.guesstheword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.guesstheword.dependencies.MyApplication
import com.example.guesstheword.screens.game.GameVM

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}