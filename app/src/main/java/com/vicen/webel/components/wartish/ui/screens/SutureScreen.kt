package com.vicen.webel.components.wartish.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.PlayerEntity
import com.vicen.webel.components.wartish.game.BattleState
import com.vicen.webel.components.wartish.game.EnemyState
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.Coral
import com.vicen.webel.components.wartish.ui.Gold
import com.vicen.webel.components.wartish.ui.Mint
import com.vicen.webel.components.wartish.ui.TextMain
import com.vicen.webel.components.wartish.ui.TextSoft
import com.vicen.webel.components.wartish.ui.WartishTheme
import com.vicen.webel.components.wartish.ui.components.GamePage
import com.vicen.webel.components.wartish.ui.components.GoldButton
import com.vicen.webel.components.wartish.ui.components.PixelPanel
import com.vicen.webel.components.wartish.ui.components.RusticBrass
import com.vicen.webel.components.wartish.ui.components.RusticParchment
import com.vicen.webel.components.wartish.ui.components.RusticWood
import com.vicen.webel.components.wartish.ui.components.RusticWoodDark

@Composable
internal fun SutureScreen(
    ui: GameUiState,
    onApplySuture: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val battle = ui.battle
    val session = battle?.entrySequence
    var woundIndex by remember(session) { mutableIntStateOf(0) }
    var successfulWounds by remember(session) { mutableIntStateOf(0) }
    var traceProgress by remember(session) { mutableIntStateOf(0) }
    var finished by remember(session) { mutableStateOf(false) }
    val hitRadius = with(LocalDensity.current) { 32.dp.toPx() }

    fun finishWound(successful: Boolean) {
        val result = successfulWounds + if (successful) 1 else 0
        successfulWounds = result
        if (woundIndex >= 2) {
            finished = true
            onApplySuture(result)
        } else {
            woundIndex++
            traceProgress = 0
        }
    }

    GamePage(
        title = "Sutura de campaña",
        subtitle = "Cose las heridas para recuperar fuerzas",
        player = ui.player,
        onBack = onBack,
        notice = ui.notice,
        backButtonAboveProfile = true,
        fillRemainingHeight = true,
        centerTitleAtTop = true,
        showBackpack = false,
        screenBackground = R.drawable.campamento_circular,
        centerHeaderStatus = battle?.let { "RONDA ${it.encounterNumber} · ${ui.player.currentHealth}/${ui.player.maxHealth} PV" } ?: "HOGUERA DEL CAMPAMENTO",
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when {
                battle == null -> PixelPanel {
                    Text("La hoguera está lista. Vuelve después de completar una etapa para atender tus heridas.", color = TextMain)
                }
                !battle.complete -> PixelPanel {
                    Text("La sutura solo se puede hacer entre etapas, cuando termina el combate.", color = TextMain)
                    Text("Tu vida actual se conservará mientras luchas.", color = TextSoft)
                }
                battle.treatmentUsed || finished -> PixelPanel {
                    Text("SUTURA TERMINADA", color = Mint, fontWeight = FontWeight.Black, fontSize = 21.sp)
                    Text("Vida actual: ${ui.player.currentHealth}/${ui.player.maxHealth} PV", color = TextMain)
                    Text("Cada herida bien cosida recupera un 10% de la vida máxima.", color = TextSoft)
                    GoldButton("VOLVER AL CAMPAMENTO", onClick = onBack)
                }
                ui.player.currentHealth >= ui.player.maxHealth -> PixelPanel {
                    Text("No tienes heridas que atender.", color = Mint, fontWeight = FontWeight.Bold)
                    Text("La vida se conserva entre etapas; vuelve a la hoguera cuando recibas daño.", color = TextSoft)
                }
                else -> PixelPanel {
                    Text("Cose la herida siguiendo los cinco puntos de izquierda a derecha.", color = TextMain, fontWeight = FontWeight.Bold)
                    Text("Herida ${woundIndex + 1} de 3 · puntadas acertadas: $successfulWounds", color = Gold, fontWeight = FontWeight.Black)
                    Text("Cada puntada completa cura un 10% de tu vida máxima.", color = TextSoft, fontSize = 12.sp)
                    BandageTrace(
                        progress = traceProgress,
                        hitRadius = hitRadius,
                        enabled = !finished,
                        onProgress = { traceProgress = it },
                        onStrokeFinished = { finishWound(it) },
                    )
                    OutlinedButton(
                        onClick = { finishWound(successful = false) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("SALTAR ESTA HERIDA", color = RusticParchment, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BandageTrace(
    progress: Int,
    hitRadius: Float,
    enabled: Boolean,
    onProgress: (Int) -> Unit,
    onStrokeFinished: (Boolean) -> Unit,
) {
    val latestProgressCallback = rememberUpdatedState(onProgress)
    val latestFinishedCallback = rememberUpdatedState(onStrokeFinished)
    Canvas(
        Modifier.fillMaxWidth().height(118.dp).then(
            if (enabled) Modifier.pointerInput(enabled, hitRadius) {
                var localProgress = 0
                detectDragGestures(
                    onDragStart = { position ->
                        val anchors = stitchAnchors(size.width.toFloat(), size.height.toFloat())
                        localProgress = if ((position - anchors.first()).getDistance() <= hitRadius) 1 else 0
                        latestProgressCallback.value(localProgress)
                    },
                    onDrag = { change, _ ->
                        val anchors = stitchAnchors(size.width.toFloat(), size.height.toFloat())
                        if (localProgress in 1 until anchors.size && (change.position - anchors[localProgress]).getDistance() <= hitRadius) {
                            localProgress++
                            latestProgressCallback.value(localProgress)
                        }
                        change.consume()
                    },
                    onDragEnd = { latestFinishedCallback.value(localProgress == 5) },
                    onDragCancel = { latestFinishedCallback.value(false) },
                )
            } else Modifier,
        ),
    ) {
        drawRoundRect(color = RusticWood, cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f))
        var lineY = 8f
        while (lineY < size.height) {
            drawLine(RusticBrass.copy(alpha = .22f), Offset(0f, lineY), Offset(size.width, lineY), strokeWidth = 1f)
            lineY += 14f
        }
        val anchors = stitchAnchors(size.width, size.height)
        val wound = Path().apply {
            moveTo(anchors.first().x, anchors.first().y)
            anchors.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(wound, color = RusticWoodDark, style = Stroke(width = 13f))
        drawPath(wound, color = Coral.copy(alpha = .8f), style = Stroke(width = 4f))
        for (index in 0 until (progress - 1).coerceAtLeast(0)) {
            drawLine(RusticParchment, anchors[index], anchors[index + 1], strokeWidth = 5f)
        }
        anchors.forEachIndexed { index, point ->
            val stitched = index < progress
            drawCircle(
                color = if (stitched) Gold else RusticBrass,
                radius = if (index == progress) 10f else 7f,
                center = point,
            )
        }
    }
}

private fun stitchAnchors(width: Float, height: Float): List<Offset> = listOf(
    Offset(width * .10f, height * .60f),
    Offset(width * .30f, height * .38f),
    Offset(width * .50f, height * .60f),
    Offset(width * .70f, height * .38f),
    Offset(width * .90f, height * .60f),
)

@Preview(name = "Sutura de campaña", showBackground = true)
@Composable
private fun SutureScreenPreview() {
    val enemy = EnemyState(level = 1, health = 0, maxHealth = 25, attack = 4, defense = 1, speed = 2)
    WartishTheme {
        SutureScreen(
            ui = GameUiState(
                battle = BattleState(
                    enemies = listOf(enemy, enemy, enemy),
                    enemyIndex = 2,
                    playerHealth = 42,
                    playerRemainingMs = 0,
                    enemyRemainingMs = 0,
                    complete = true,
                    won = true,
                ),
                player = PlayerEntity(currentHealth = 42),
            ),
            onApplySuture = {},
            onBack = {},
        )
    }
}
