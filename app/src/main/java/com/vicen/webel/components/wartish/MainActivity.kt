package com.vicen.webel.components.wartish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vicen.webel.components.wartish.game.GameViewModel
import com.vicen.webel.components.wartish.ui.WartishApp

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WartishApp(viewModel) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.resumeFromBackground()
    }

    override fun onStop() {
        viewModel.pauseForBackground()
        super.onStop()
    }
}
