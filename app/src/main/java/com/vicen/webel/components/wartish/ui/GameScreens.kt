package com.vicen.webel.components.wartish.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vicen.webel.components.wartish.game.GameViewModel
import com.vicen.webel.components.wartish.ui.screens.*
import kotlinx.coroutines.delay

private object Route {
    const val HOME = "home"
    const val COLLECT = "collect"
    const val PROCESS = "process"
    const val BATTLE = "battle"
    const val SMITH = "smith"
    const val SUTURE = "suture"
}


@Composable
fun WartishApp(viewModel: GameViewModel) {
    val nav = rememberNavController()
    val ui by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(ui.notice) {
        if (ui.notice != null) {
            delay(3000)
            viewModel.clearNotice()
        }
    }
    WartishTheme {
        Scaffold(containerColor = Ink) { insets ->
            NavHost(navController = nav, startDestination = Route.HOME, modifier = Modifier.padding(insets)) {
                composable(Route.HOME) {
                    HomeScreen(
                        ui,
                        onCollect = { viewModel.openCollection(); nav.navigate(Route.COLLECT) },
                        onProcess = { viewModel.openProcessing(); nav.navigate(Route.PROCESS) },
                        onBattle = { viewModel.openBattle(); nav.navigate(Route.BATTLE) },
                        onSmith = { nav.navigate(Route.SMITH) },
                        onSuture = { viewModel.openBattle(); nav.navigate(Route.SUTURE) },
                    )
                }
                composable(Route.COLLECT) {
                    CollectionScreen(
                        ui,
                        CollectionScreenActions(
                            restart = { viewModel.restartCollection() },
                            clearAnimation = { viewModel.clearCollectionAnimation(it) },
                            swapCells = { first, second, resolved -> viewModel.swapCells(first, second, resolved) },
                            chooseCell = { viewModel.chooseCell(it) },
                        ),
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Route.PROCESS) {
                    ProcessingScreen(
                        ui,
                        ProcessingScreenActions(
                            pause = { viewModel.pauseProcessing() },
                            tick = { viewModel.tickFalling() },
                            completeClear = { viewModel.completeProcessingClear(it) },
                            move = { viewModel.moveFalling(it) },
                            rotate = { viewModel.rotateFalling() },
                            startOrResume = { viewModel.startOrResumeProcessing() },
                        ),
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Route.BATTLE) {
                    BattleScreen(
                        ui,
                        BattleScreenActions(
                            start = { viewModel.startBattle() },
                            completeEntry = { viewModel.completeBattleEntry(it) },
                            useSpecial = { viewModel.useSpecial() },
                            pause = { viewModel.pauseBattle() },
                            resume = { viewModel.resumeBattle() },
                        ),
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Route.SMITH) {
                    BlacksmithScreen(
                        ui,
                        BlacksmithScreenActions(
                            selectEquipment = { viewModel.showUpgrade(it) },
                            upgrade = { viewModel.upgrade(it) },
                            dismissUpgrade = { viewModel.dismissUpgrade() },
                        ),
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Route.SUTURE) {
                    SutureScreen(
                        ui = ui,
                        onApplySuture = { viewModel.applySuture(it) },
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }
    }
}
